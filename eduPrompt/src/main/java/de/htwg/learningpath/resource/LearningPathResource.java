package de.htwg.learningpath.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.htwg.ai.dto.CourseLLMDTO;
import de.htwg.ai.services.EvaluationService;
import de.htwg.ai.services.learningstep.GenerateReadingMaterialService;
import de.htwg.ai.services.learningstep.GetTasksService;
import de.htwg.ai.services.learningstep.SkippedInitAssessmentReadingMaterialService;
import de.htwg.course.model.BloomLevel;
import de.htwg.course.model.Course;
import de.htwg.course.model.Task;
import de.htwg.course.model.Topic;
import de.htwg.learningpath.dto.CourseWithoutTaskDTO;
import de.htwg.learningpath.dto.LLMUserTaskAttemptDTO;
import de.htwg.learningpath.dto.LearningPathGetDTO;
import de.htwg.learningpath.dto.LearningStepGetDTO;
import de.htwg.learningpath.model.*;
import de.htwg.learningpath.services.EvaluationMapper;
import de.htwg.learningpath.services.LearningPathService;
import de.htwg.learningpath.services.LearningStepService;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Path("learningpath")
@ActivateRequestContext
public class LearningPathResource {

    @Inject
    LearningPathService learningPathService;

    @Inject
    EvaluationService evaluationService;

    @Inject
    GenerateReadingMaterialService generateReadingMaterialService;

    @Inject
    SkippedInitAssessmentReadingMaterialService skippedInitAssessmentReadingMaterialService;

    @Inject
    GetTasksService getTasksService;

    @Inject
    LearningStepService learningStepService;
    @Inject
    ObjectMapper objectMapper;

    @GET
    @Path("/{id}")
    public Response getLearningPath(@PathParam("id") Long id) {
        LearningPath learningPath = LearningPath.findById(id);
        if (learningPath == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(new LearningPathGetDTO(learningPath)).build();
    }


    @POST
    @Path("/firststep/{id}")
    public Response generateFirstStep(@PathParam("id") Long learningPathId, @QueryParam("skip") @DefaultValue("false") boolean skip) {
        try {
            LearningPath learningPath = LearningPath.findById(learningPathId);
            if (learningPath == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            if (learningPath.course == null || learningPath.course.assessment == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Assessment not found for the course").build();
            }

            List<Topic> topics = learningPath.course.topics;

            if (topics == null || topics.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("No topics found for the course").build();
            }

            if (skip) {
                // Handle the skip case: Create LearningStatistics for each topic with default values
                StringBuilder statisticsString = new StringBuilder();
                List<LearningStatistics> statisticsList = new ArrayList<>();
                for (Topic topic : topics) {
                    LearningStatistics statistics = new LearningStatistics();
                    statistics.user = learningPath.user;
                    statistics.learningPath = learningPath;
                    statistics.topic = topic;
                    statistics.currentBloomLevel = BloomLevel.NONE;
                    statistics.strengths = "-";
                    statistics.weaknesses = "-";
                    statistics.recommendations = "-";
                    learningStepService.persistStats(statistics);

                    statisticsList.add(statistics);

                    statisticsString.append("Topic: ").append(topic.name).append("\n")
                            .append("  Bloom Level: ").append(statistics.currentBloomLevel).append("\n")
                            .append("  Strengths: ").append(statistics.strengths).append("\n")
                            .append("  Weaknesses: ").append(statistics.weaknesses).append("\n")
                            .append("  Recommendations: ").append(statistics.recommendations).append("\n")
                            .append("\n");
                }

                learningPath.initialAssessmentCompleted = true;
                // Generate reading material for the skipped assessment case
                String courseJson = objectMapper.writeValueAsString(new CourseLLMDTO(learningPath.course));
                String memoryId = "user" + learningPath.user.id + "-lp-" + learningPathId + "-init";
                String language = (learningPath.user.preferredLanguage != null) ? learningPath.user.preferredLanguage : "DE";
                String evalRes = "The User has no knowledge of any content in this course. Start from the very start with the first topic.";
                String readingMaterialJson = skippedInitAssessmentReadingMaterialService.generateReadingMaterial(memoryId, courseJson, "The User has no knowledge of any content in this course. Start from the very start with the first topic.", language);

                List<Topic> topicsForLearningStep = learningPathService.getTopicsForLearningStep(topics, statisticsList);
                String tasksString = topicsForLearningStep.stream()
                        .flatMap(topic -> topic.tasks.stream())
                        .map(Task::toString)
                        .collect(Collectors.joining("\n"));

                // Process the reading material and tasks
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(readingMaterialJson);
                String explanationText = jsonNode.get("explanationText").asText();
                String readingMaterial = jsonNode.get("readingMaterial").asText();

                String tasksJson = getTasksService.getTasksForNoKnowledge(explanationText, readingMaterial, learningPath.user.id.toString(), courseJson, String.valueOf(statisticsString), tasksString);

                // Parse the tasksJson to get task IDs
                JsonNode tasksJsonNode = mapper.readTree(tasksJson);
                List<Long> taskIds = new ArrayList<>();
                tasksJsonNode.get("taskIds").forEach(taskIdNode -> taskIds.add(taskIdNode.asLong()));
                List<Task> tasks = learningStepService.getTasksByIds(taskIds);
                LearningStep newLearningStep = learningStepService.createLearningStep(learningPath, tasks, readingMaterial, explanationText);
                return Response.ok(newLearningStep).build();
            } else {
                // Handle the regular flow with UserTaskAttempts and evaluation
                Log.info("STARTING INITIAL FIRST STEP GENERATION...");
                List<UserTaskAttempt> attempts = UserTaskAttempt.list("user.id = ?1 and assessment.id = ?2", learningPath.user.id, learningPath.course.assessment.id);
                if (attempts == null || attempts.isEmpty()) {
                    return Response.status(Response.Status.NOT_FOUND).entity("No task attempts found for the user and assessment").build();
                }
                // Convert UserTaskAttempt list to LLMUserTaskAttemptDTO list
                List<LLMUserTaskAttemptDTO> llmTaskAttempts = attempts.stream()
                        .map(learningPathService::toLLMDTO)  // Convert each attempt to LLMUserTaskAttemptDTO
                        .collect(Collectors.toList());

                // Convert the list to a string representation
                String attemptsString = llmTaskAttempts.stream()
                        .map(LLMUserTaskAttemptDTO::toString)
                        .collect(Collectors.joining("\n"));

                Log.info("ATTEMPTS STRING");
                Log.info(attemptsString);


//                String attemptsString = attempts.stream().map(attempt -> attempt.toString()).collect(Collectors.joining("\n"));
                String topicsString = topics.stream().map(topic -> topic.toString()).collect(Collectors.joining("\n"));
                String taskCompletionString = learningPathService.generateTaskCompletionString(learningPath.user.id, learningPath.id, learningPath.course.id);

                String evaluationResponse = evaluationService.evaluateInitialAssessment(topicsString, taskCompletionString, attemptsString);
                learningPath.initialEvaluation = evaluationResponse;
                Log.info("INITIAL EVAL");
                Log.info(evaluationResponse);

                EvaluationMapper evalMapper = new EvaluationMapper();
                List<LearningStatistics> statisticsList = evalMapper.mapEvaluationResponse(evaluationResponse, learningPath.user, learningPath, topics);
                for (LearningStatistics stats : statisticsList) {
                    learningStepService.persistStats(stats);
                }
                List<Topic> topicsForLearningStep = learningPathService.getTopicsForLearningStep(topics, statisticsList);
                String tasksString = topicsForLearningStep.stream()
                        .flatMap(topic -> topic.tasks.stream())
                        .map(Task::toString)
                        .collect(Collectors.joining("\n"));


                StringBuilder currentAndNextTopic = new StringBuilder();
                if (topicsForLearningStep.size() > 0) {
                    currentAndNextTopic.append("Current Topic: ").append(topicsForLearningStep.get(0).id);
                }
                if (topicsForLearningStep.size() > 1) {
                    currentAndNextTopic.append(" | Next Topic: ").append(topicsForLearningStep.get(1).id);
                }
                String currentAndNextTopicString = currentAndNextTopic.toString();
                Log.info("Formatted Topics String: \n" + currentAndNextTopicString);

                String courseJson = objectMapper.writeValueAsString(new CourseWithoutTaskDTO(learningPath.course));
                String memoryId = "user" + learningPath.user.id + "-lp-" + learningPathId + "-reading";
                String language = (learningPath.user.preferredLanguage != null) ? learningPath.user.preferredLanguage : "DE";
                String readingMaterialJson = generateReadingMaterialService.generateReadingMaterial(memoryId, courseJson, evaluationResponse, language, attemptsString, currentAndNextTopicString, "-");

                // Extract explanationText and readingMaterial from LLM Response
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(readingMaterialJson);
                String explanationText = jsonNode.get("explanationText").asText();
                String readingMaterial = jsonNode.get("readingMaterial").asText();


                // Call LLM Service to grab fitting tasks for the learningstep
                String getTaskMemoryId = "getTask-lp" + learningPath.id + "-user-" + learningPath.user.id;
                String tasksJson = getTasksService.getTasksInitial(getTaskMemoryId, explanationText, readingMaterial, learningPath.user.id.toString(), courseJson, attemptsString, evaluationResponse, currentAndNextTopicString, tasksString);
                JsonNode tasksJsonNode = mapper.readTree(tasksJson);
                List<Long> taskIds = new ArrayList<>();
                tasksJsonNode.get("taskIds").forEach(taskIdNode -> taskIds.add(taskIdNode.asLong()));
                List<Task> tasks = learningStepService.getTasksByIds(taskIds);
                LearningStep newLearningStep = learningStepService.createLearningStep(learningPath, tasks, readingMaterial, explanationText);
                return Response.ok(newLearningStep).build();
            }

        } catch (Exception e) {
            Log.error("Error occurred while generating first step", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error occurred: " + e.getMessage()).build();
        }
    }


    @POST
    @Path("/nextstep/{id}")
    public Response generateNextStepAsync(@PathParam("id") Long learningPathId) {
        Log.info("NEXT STEP!");
        try {
            // Fetch Learning Path
            LearningPath learningPath = LearningPath.findById(learningPathId);
            if (learningPath == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Learning path not found").build();
            }

            LearningStep lastLearningStep = LearningStep.find("learningPath.id = ?1 and id is not null order by id desc", learningPath.id).firstResult();
            if (lastLearningStep == null) {
                lastLearningStep = LearningStep.find("learningPath.id = ?1 and id is null", learningPath.id).firstResult();
            }
            if (lastLearningStep == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("No previous learning step found for the learning path").build();
            }

            // Get all Topics of the Course
            List<Topic> topics = learningPath.course.topics;
            if (topics == null || topics.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("No topics found for the course").build();
            }

            // Retrieve Task Attempts for the last LearningStep
            List<UserTaskAttempt> attempts = UserTaskAttempt.list("user.id = ?1 and learningStep.id = ?2", learningPath.user.id, lastLearningStep.id);
            if (attempts == null || attempts.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND).entity("No task attempts found for the user and the last learning step").build();
            }

            // Build strings for logging/debugging
            String attemptsString = attempts.stream().map(UserTaskAttempt::toString).collect(Collectors.joining("\n"));
            String topicsString = topics.stream().map(Topic::toString).collect(Collectors.joining("\n"));

            // Get learning statistics for the previous step
            List<LearningStatistics> learningStatistics = LearningStatistics.list("user.id = ?1 and learningPath.id = ?2 and learningStep.id = ?3",
                    learningPath.user.id, learningPath.id, lastLearningStep.id);
            Log.info(learningStatistics);
            String learningStatisticsString = learningStatistics.stream().map(LearningStatistics::toString).collect(Collectors.joining("\n"));
            Log.info(learningStatisticsString);

            // Convert the Course to JSON
            Course course = Course.findById(learningPath.course.id);
            String courseJson = objectMapper.writeValueAsString(new CourseLLMDTO(course));

            // Generate task completion string
            String taskCompletionString = learningPathService.generateTaskCompletionString(learningPath.user.id, learningPath.id, learningPath.course.id);
//            Log.info("Task Completion String: " + taskCompletionString);

            // Fetch the latest LLM feedback, if any
            Feedback latestLLMFeedback = Feedback.find("learningPath.id = ?1 and feedbackType = ?2 and isIngestedToLLM = false order by createdAt desc", learningPath.id, Feedback.FeedbackType.LLM).firstResult();
            String feedbackContent = (latestLLMFeedback != null) ? latestLLMFeedback.content : "";
//            Log.info(feedbackContent);


            // Evaluate the student's previous step
            String evaluationMemoryId = "eval-lp-" + learningPath.id + "-user-" + learningPath.user.id;
            String evaluationResponse = evaluationService.evaluate(evaluationMemoryId, attemptsString, topicsString, learningStatisticsString, taskCompletionString, learningPath.course.courseSettings.language);
//            Log.info("Evaluation response: " + evaluationResponse);

            // Map evaluation response to LearningStatistics and persist them
            EvaluationMapper evalMapper = new EvaluationMapper();
            List<LearningStatistics> statisticsList = evalMapper.mapEvaluationResponse(evaluationResponse, learningPath.user, learningPath, topics);


            List<Topic> topicsForLearningStep = learningPathService.getTopicsForLearningStep(topics, statisticsList);
            Log.info("TOPICS OF CUR");
            Log.info(topicsForLearningStep);
            String tasksString = topicsForLearningStep.stream()
                    .flatMap(topic -> topic.tasks.stream())
                    .map(Task::toString)
                    .collect(Collectors.joining("\n"));
            Log.info("TASKS FOR TOPIC:");
            Log.info(tasksString);


            StringBuilder currentAndNextTopic = new StringBuilder();
            if (topicsForLearningStep.size() > 0) {
                currentAndNextTopic.append("Current Topic: ").append(topicsForLearningStep.get(0).id);
            }
            if (topicsForLearningStep.size() > 1) {
                currentAndNextTopic.append(" | Next Topic: ").append(topicsForLearningStep.get(1).id);
            }
            String currentAndNextTopicString = currentAndNextTopic.toString();
            Log.info("Formatted Topics String: \n" + currentAndNextTopicString);


            String courseString = objectMapper.writeValueAsString(new CourseWithoutTaskDTO(course));
            Log.info(courseString);

            // Call LLM to generate tasks and reading material
            String memoryId = "user" + learningPath.user.id + "-lp-" + learningPathId + "-next";
            String language = (learningPath.user.preferredLanguage != null) ? learningPath.user.preferredLanguage : "DE";
            String readingMaterialJson = generateReadingMaterialService.generateReadingMaterial(memoryId, courseString, evaluationResponse, language, attemptsString, currentAndNextTopicString, feedbackContent);
//             TODO: define mapper that maps outputs of llm services to given DTO
//
//             Extract explanation text and reading material from the response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(readingMaterialJson);
            String explanationText = jsonNode.get("explanationText").asText();
            String readingMaterial = jsonNode.get("readingMaterial").asText();

            // Call the LLM to get tasks for this step based on reading material
            String getTaskMemoryId = "getTask-lp" + learningPath.id + "-user-" + learningPath.user.id;
            String tasksJson = getTasksService.getTasks(getTaskMemoryId, explanationText, readingMaterial, learningPath.user.id.toString(), courseString, attemptsString, learningStatisticsString, currentAndNextTopicString, tasksString);

            // Parse tasks from tasksJson and get the task list
            JsonNode tasksJsonNode = mapper.readTree(tasksJson);
            List<Long> taskIds = new ArrayList<>();
            tasksJsonNode.get("taskIds").forEach(taskIdNode -> taskIds.add(taskIdNode.asLong()));
            List<Task> tasksForLearningStep = learningStepService.getTasksByIds(taskIds);


            // Create a new LearningStep using the parsed tasks, explanation, and reading material
            LearningStep newLearningStep = learningStepService.createLearningStep(learningPath, tasksForLearningStep, readingMaterial, explanationText);

            // Link the new step with the previous step
            lastLearningStep.nextStep = newLearningStep;
            newLearningStep.previousStep = lastLearningStep;
//
//            // Persist the updates
            learningStepService.persist(lastLearningStep);
            learningStepService.persist(newLearningStep);

            for (LearningStatistics stats : statisticsList) {
                stats.learningStep = newLearningStep;
                learningStepService.persistStats(stats);
            }

            // Mark feedback as ingested, if applicable
            if (latestLLMFeedback != null) {
                latestLLMFeedback.isIngestedToLLM = true;
                latestLLMFeedback.persist();
            }

            // Return the new learning step DTO
            LearningStepGetDTO learningStepGetDTO = new LearningStepGetDTO(newLearningStep);
            return Response.ok(learningStepGetDTO).build();

        } catch (Exception e) {
            Log.error("Error occurred while generating next step", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error occurred: " + e.getMessage()).build();
        }
    }

    private List<TopicResult> generateTopicsConcurrently(List<Topic> topics, List<LearningStatistics> statistics, String courseJson, String evaluationResponse, Long userId, Long courseId) throws InterruptedException, java.util.concurrent.ExecutionException {
        Map<Long, LearningStatistics> topicStatisticsMap = statistics.stream()
                .collect(Collectors.toMap(stat -> stat.topic.id, stat -> stat));
        // Run the generateTopic calls concurrently
        List<CompletableFuture<TopicResult>> futures = topics.stream()
                .map(topic -> CompletableFuture.supplyAsync(() -> {
                    try {
                        LearningStatistics topicStatistic = topicStatisticsMap.get(topic.id);
                        return generateTopic(topic, courseJson, evaluationResponse, userId, courseId, topicStatistic);
                    } catch (Exception e) {
                        Log.error("Error generating topic for: " + topic, e);
                        return null;
                    }
                }))
                .collect(Collectors.toList());

        // Combine all futures to a single future that completes when all of them complete
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allOf.join();

        // Collect the results
        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    private TopicResult generateTopic(Topic topic, String courseJson, String evaluationResponse, Long userId, Long courseId, LearningStatistics statistic) throws Exception {
        String memoryId = userId + "-" + topic.id;
        String readingMaterialJson = generateReadingMaterialService.generateTopicReadingMaterial(memoryId, topic.toString(), evaluationResponse);

        JsonNode jsonNode = objectMapper.readTree(readingMaterialJson);
        String explanationText = jsonNode.get("explanationText").asText();
        String readingMaterial = jsonNode.get("readingMaterial").asText();

//        String tasksJson = generateTasksService.generateTasksForTopic(userId + "-" + topic.id, explanationText, readingMaterial, userId.toString(), topic.toString(), courseId.toString(), statistic.toString());
        String tasksJson = getTasksService.getTasksForLearningStep("taskgen-" + userId + "-" + topic.id, explanationText, readingMaterial, userId.toString(), topic.toString(), courseId.toString(), statistic.toString());
        Log.info("Generated tasks for topic " + topic.name + ": " + tasksJson);

        return new TopicResult(readingMaterialJson, tasksJson);
    }

    private String generateTasksFromResults(List<TopicResult> results) {
        String combinedTasksJson = results.stream()
                .map(TopicResult::getTasksJson)
                .collect(Collectors.joining("\n"));

        String combinedReadingMaterials = results.stream()
                .map(TopicResult::getReadingMaterialJson)
                .collect(Collectors.joining("\n"));

        // This is just a placeholder, adapt as necessary
        return combinedTasksJson;
    }


    @GET
    @Path("/user/{id}")
    public Response getLearningPathsByUser(@PathParam("id") Long id) {
        List<LearningPath> learningPaths = learningPathService.findLearningPathsByUserId(id);
        List<LearningPathGetDTO> dtoList = learningPaths.stream()
                .map(LearningPathGetDTO::new)
                .collect(Collectors.toList());
        return Response.ok(dtoList).build();
    }

    @GET
    @Path("/user/{id}/path/{pathId}")
    public Response getLearningPathsByUser(@PathParam("id") Long userId, @PathParam("pathId") Long pathId) {
        LearningPath learningPath = LearningPath.findById(pathId);
        if (learningPath == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (!learningPath.user.id.equals(userId)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        LearningPathGetDTO learningPathDTO = new LearningPathGetDTO(learningPath);
        return Response.ok(learningPathDTO).build();
    }


}
