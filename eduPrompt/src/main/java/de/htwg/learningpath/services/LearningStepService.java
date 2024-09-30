package de.htwg.learningpath.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import de.htwg.ai.dto.LearningStepDTO;
import de.htwg.course.dto.initassessment.AssessmentDTO;
import de.htwg.course.dto.initassessment.TaskDTO;
import de.htwg.course.dto.initassessment.TopicDTO;
import de.htwg.course.model.*;
import de.htwg.course.model.tasktype.*;
import de.htwg.course.services.TaskService;
import de.htwg.learningpath.dto.TaskJsonDTO;
import de.htwg.learningpath.model.LearningPath;
import de.htwg.learningpath.model.LearningStatistics;
import de.htwg.learningpath.model.LearningStep;
import de.htwg.learningpath.model.TopicResult;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class LearningStepService {

    @Inject
    EntityManager em;

    @Inject
    TaskService taskService;


    @Transactional
    public LearningStep createLearningStep2(List<TopicResult> topicResults, LearningPath learningPath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            // Concatenate reading material and explanation text from all topic results
            StringBuilder combinedReadingMaterial = new StringBuilder();
            StringBuilder combinedExplanationText = new StringBuilder();

            for (TopicResult topicResult : topicResults) {
                JsonNode jsonNode = mapper.readTree(topicResult.getReadingMaterialJson());
                combinedReadingMaterial.append(jsonNode.get("readingMaterial").asText()).append("\n\n---\n\n");
                combinedExplanationText.append(jsonNode.get("explanationText").asText()).append("\n\n---\n\n");
            }

            LearningStep learningStep = new LearningStep();
            learningStep.readingMaterial = combinedReadingMaterial.toString();
            learningStep.explanationText = combinedExplanationText.toString();
            learningStep.learningPath = learningPath;

            Log.info("LearningPath: " + learningPath);
            Log.info("LearningStep: " + learningStep);

            for (TopicResult topicResult : topicResults) {
                JsonNode jsonNode = mapper.readTree(topicResult.getTasksJson());
                Log.info("JSON Node: " + jsonNode.toString());
                LearningStepDTO learningStepDTO = mapper.treeToValue(jsonNode, LearningStepDTO.class);

                // Add existing tasks by their IDs
                if (learningStepDTO.existingTasks != null) {
                    List<Task> existingTasks = Task.list("id in (?1)", learningStepDTO.existingTasks);
                    learningStep.tasks.addAll(existingTasks);
                }

                for (TopicDTO topicDTO : learningStepDTO.topics) {
                    Topic topic = em.find(Topic.class, topicDTO.getTopicId());
                    if (topic != null) {
                        for (TaskDTO taskDTO : topicDTO.getTasks()) {
                            Task task = createTaskFromLlmDTO(taskDTO);
                            task.topic = topic;
                            task.verified = false;
                            Log.info("TASK: " + task);
                            learningStep.tasks.add(task);
                            em.persist(task);
                        }
                    }
                }
            }

            em.persist(learningStep);
            em.flush();  // Ensure data is written to the database

            Log.info("Persisted LearningStep: " + learningStep);
            return learningStep;

        } catch (Exception e) {
            Log.error("Error in createLearningStep", e);
            throw new RuntimeException("Error creating learning step", e);
        }
    }

    @Transactional
    public List<Task> getTasksByIds(List<Long> taskIds) {
        if (taskIds == null || taskIds.isEmpty()) {
            Log.warn("No task IDs provided to retrieve tasks.");
            return Collections.emptyList();
        }

        // Retrieve tasks by their IDs
        List<Task> tasks = Task.list("id in (?1)", taskIds);
        Log.info("Retrieved tasks: " + tasks);
        return tasks;
    }

    @Transactional
    public LearningStep createLearningStep(LearningPath learningPath, List<Task> tasks, String readingMaterial, String explanationText) {
        try {
            LearningStep learningStep = new LearningStep();
            learningStep.readingMaterial = readingMaterial;
            learningStep.explanationText = explanationText;
            learningStep.learningPath = learningPath;

            // Merge tasks to avoid detached entity issues
            for (Task task : tasks) {
                Task managedTask = em.merge(task); // Attach the detached task
                learningStep.tasks.add(managedTask);
            }

            em.persist(learningStep);
            em.flush();


            Log.info("Created LearningStep: " + learningStep);
            return learningStep;
        } catch (Exception e) {
            Log.error("Error in creating LearningStep", e);
            throw new RuntimeException("Error creating learning step", e);
        }
    }


    @Transactional
    public LearningStep createLearningStep1(String llmResponse, LearningPath learningPath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            LearningStepDTO learningStepDTO = mapper.readValue(llmResponse, new TypeReference<LearningStepDTO>() {
            });
            Log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//            Log.info(learningStepDTO.existingTasks);
            LearningStep learningStep = new LearningStep();
            learningStep.readingMaterial = learningStepDTO.readingMaterial;
            learningStep.explanationText = learningStepDTO.explanationText;
            learningStep.learningPath = learningPath;
//            learningPath.learningSteps.add(learningStep);
            Log.info("LearningPath: " + learningPath);
            Log.info("LearningStep: " + learningStep);

            // Add existing tasks by their IDs
            if (learningStepDTO.existingTasks != null) {
                List<Task> existingTasks = Task.list("id in (?1)", learningStepDTO.existingTasks);
                learningStep.tasks.addAll(existingTasks);
            }
            Log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!");

            // TODO: create a method that gets all Tasks by the list of ids of existing tasks and adds them to tasks of learning step
            // Run this with no pre-created tasks


            for (TopicDTO topicDTO : learningStepDTO.topics) {
                Topic topic = em.find(Topic.class, topicDTO.getTopicId());
                if (topic != null) {
                    for (TaskDTO taskDTO : topicDTO.getTasks()) {
                        Task task = createTaskFromLlmDTO(taskDTO);
                        task.topic = topic;
                        task.verified = false;
                        Log.info("TASK: " + task);
                        learningStep.tasks.add(task);
                        em.persist(task);
                    }
                }
            }
            em.persist(learningStep);
            em.flush();  // Ensure data is written to the database

            Log.info("Persisted LearningStep: " + learningStep);
            return learningStep;

        } catch (Exception e) {
            Log.error("Error in createLearningStep", e);
            throw new RuntimeException("Error creating learning step", e);
        }
    }

    @Transactional
    public void persist(LearningStep learningStep) {
        em.merge(learningStep);
        em.flush();
    }

    @Transactional
    public void persistStats(LearningStatistics learningStatistics) {
        em.persist(learningStatistics);
    }

    //    private Task createTaskFromLlmDTO(de.htwg.course.dto.TaskDTO taskDTO) {
    private Task createTaskFromLlmDTO(de.htwg.course.dto.initassessment.TaskDTO taskDTO) {
        Task task;
        switch (taskDTO.getTaskType().toUpperCase()) {
            case "CODING":
                CodingTask codingTask = new CodingTask();
                codingTask.setProgrammingLanguage(taskDTO.getLanguage());
                task = codingTask;
                break;
            case "MULTIPLE_CHOICE":
                MultipleChoiceTask multipleChoiceTask = new MultipleChoiceTask();
                multipleChoiceTask.setOptions(taskDTO.getOptions()); // Assuming JSON handling is encapsulated
                task = multipleChoiceTask;
                break;
            case "MATCHING":
                MatchingTask matchingTask = new MatchingTask();
                matchingTask.setOptions(taskDTO.getOptions()); // Assuming JSON handling is encapsulated
                task = matchingTask;
                break;
            case "SORTING":
                SortingTask sortingTask = new SortingTask();
                sortingTask.setOptions(taskDTO.getOptions()); // Assuming JSON handling is encapsulated
                task = sortingTask;
                break;
            case "DRAG_AND_DROP":
                DragAndDropTask dragAndDropTask = new DragAndDropTask();
                dragAndDropTask.setOptions(taskDTO.getOptions()); // Assuming JSON handling is encapsulated
                task = dragAndDropTask;
                break;
            case "FREE_TEXT":
                FreeTextTask freeTextTask = new FreeTextTask();
                task = freeTextTask;
                break;
            case "SHORT_ANSWER":
                ShortAnswerTask shortAnswerTask = new ShortAnswerTask();
                task = shortAnswerTask;
                break;
            case "FILL_IN_THE_BLANKS":
                FillInTheBlanksTask fillInTheBlanksTask = new FillInTheBlanksTask();
                task = fillInTheBlanksTask;
                break;
            case "TRUE_FALSE":
                TrueFalseTask trueFalseTask = new TrueFalseTask();
                task = trueFalseTask;
                break;
            case "CODE_COMPLETION":
                CodeCompletionTask codeCompletionTask = new CodeCompletionTask();
                task = codeCompletionTask;
                break;
            case "ESSAY":
                EssayTask essayTask = new EssayTask();
                task = essayTask;
                break;
            default:
                throw new IllegalArgumentException("Unsupported task type: " + taskDTO.getTaskType());
        }
        task.question = taskDTO.getQuestion();
//        task.correctAnswer = taskDTO.getCorrectAnswer().toString();
        if (taskDTO.getCorrectAnswer() != null) {
            task.correctAnswer = convertCorrectAnswer(taskDTO.getCorrectAnswer());
        } else {
            task.correctAnswer = "";
        }
        task.bloomLevel = BloomLevel.valueOf(taskDTO.getBloomLevel()); // Assuming you have an enum for BloomLevel
        return task;
    }

    private String convertCorrectAnswer(Object correctAnswer) {
        if (correctAnswer instanceof String) {
            return (String) correctAnswer;
        } else if (correctAnswer instanceof Map) {
            return correctAnswer.toString(); // You may want to convert this to a specific format if needed
        } else {
            throw new IllegalArgumentException("Unsupported correctAnswer type: " + correctAnswer.getClass().getName());
        }
    }




    private Task createTaskFromDTO(TaskJsonDTO taskDTO) throws JsonProcessingException {
        Task task;
        switch (taskDTO.getTaskType().toUpperCase()) {
            case "MULTIPLE_CHOICE":
                MultipleChoiceTask multipleChoiceTask = new MultipleChoiceTask();
                multipleChoiceTask.setOptions(taskDTO.getOptions());
                task = multipleChoiceTask;
                break;
            case "MATCHING":
                MatchingTask matchingTask = new MatchingTask();
                matchingTask.setOptions(taskDTO.getOptions());
                task = matchingTask;
                break;
            case "SORTING":
                SortingTask sortingTask = new SortingTask();
                sortingTask.setOptions(taskDTO.getOptions());
                task = sortingTask;
                break;
            case "DRAG_AND_DROP":
                DragAndDropTask dragAndDropTask = new DragAndDropTask();
                dragAndDropTask.setOptions(taskDTO.getOptions());
                task = dragAndDropTask;
                break;
            case "FREE_TEXT":
                FreeTextTask freeTextTask = new FreeTextTask();
                task = freeTextTask;
                break;
            case "SHORT_ANSWER":
                ShortAnswerTask shortAnswerTask = new ShortAnswerTask();
                task = shortAnswerTask;
                break;
            case "FILL_IN_THE_BLANKS":
                FillInTheBlanksTask fillInTheBlanksTask = new FillInTheBlanksTask();
                task = fillInTheBlanksTask;
                break;
            case "TRUE_FALSE":
                TrueFalseTask trueFalseTask = new TrueFalseTask();
                task = trueFalseTask;
                break;
            case "CODE_COMPLETION":
                CodeCompletionTask codeCompletionTask = new CodeCompletionTask();
                task = codeCompletionTask;
                break;
            case "ESSAY":
                EssayTask essayTask = new EssayTask();
                task = essayTask;
                break;
            default:
                throw new IllegalArgumentException("Unsupported task type: " + taskDTO.getTaskType());
        }
        task.question = taskDTO.getQuestion();
//        task.correctAnswer = taskDTO.getCorrectAnswer().toString();
        if (taskDTO.getCorrectAnswer() instanceof String) {
            task.correctAnswer = (String) taskDTO.getCorrectAnswer();
        } else if (taskDTO.getCorrectAnswer() instanceof Map) {
            ObjectMapper mapper = new ObjectMapper();
            task.correctAnswer = mapper.writeValueAsString(taskDTO.getCorrectAnswer());
        }
        task.bloomLevel = BloomLevel.valueOf(taskDTO.getBloomLevel());
//        em.persist(task);
        return task;
    }

}
