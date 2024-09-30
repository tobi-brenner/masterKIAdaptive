package de.htwg.learningpath.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.htwg.ai.dto.TaskEvaluationResponse;
import de.htwg.ai.services.TaskEvaluationService;
import de.htwg.learningpath.dto.TaskAttemptDTO;
import de.htwg.learningpath.dto.UserTaskAttemptDTO;
import de.htwg.learningpath.model.LearningPath;
import de.htwg.learningpath.model.UserTaskAttempt;
import de.htwg.learningpath.model.TaskAnswer;
import de.htwg.course.model.Task;
import de.htwg.user.dto.TaskUserAttemptDTO;
import de.htwg.user.dto.TaskUserAttemptsCountDTO;
import de.htwg.user.model.User;
import de.htwg.course.model.Assessment;
import de.htwg.learningpath.model.LearningStep;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/attempts")
@Consumes(MediaType.APPLICATION_JSON)
public class UserTaskAttemptResource {

    @Inject
    TaskEvaluationService taskEvaluationService;

    @GET
    @Path("/user/{userId}/assessment/{assessmentId}")
    @Transactional
    public Response getUserAttemptsForAssessment(@PathParam("userId") Long userId, @PathParam("assessmentId") Long assessmentId) {
        List<UserTaskAttempt> attempts = UserTaskAttempt.list("user.id = ?1 and assessment.id = ?2", userId, assessmentId);
        List<UserTaskAttemptDTO> response = attempts.stream()
                .map(UserTaskAttemptDTO::new)
                .collect(Collectors.toList());

        return Response.ok(response).build();
    }


    @GET
    @Path("/user/{userId}/learningstep/{learningStepId}")
    @Transactional
    public Response getUserAttemptsForLearningStep(@PathParam("userId") Long userId, @PathParam("learningStepId") Long learningStepId) {
        List<UserTaskAttempt> attempts = UserTaskAttempt.list("user.id = ?1 and learningStep.id = ?2", userId, learningStepId);
        List<UserTaskAttemptDTO> response = attempts.stream()
                .map(UserTaskAttemptDTO::new)
                .collect(Collectors.toList());

        return Response.ok(response).build();
    }

    @POST
    @Transactional
    public Response saveTaskAttempt(TaskAttemptDTO taskAttemptDTO) {



        Log.info(taskAttemptDTO);
        String memoryId = "";
        // Find the user
        User user = User.findById(taskAttemptDTO.userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();

        }
        Hibernate.initialize(user.learningPaths);
        Hibernate.initialize(user.attempts);

        // Find the task
        Task task = Task.findById(taskAttemptDTO.taskId);
        if (task == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Task not found").build();
        }

        LearningPath learningPath = LearningPath.findById(taskAttemptDTO.learningPathId);
        if (learningPath == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Learningpath not found").build();

        }

        // Create a new UserTaskAttempt
        UserTaskAttempt attempt = new UserTaskAttempt();
        attempt.user = user;
        attempt.task = task;
        attempt.learningPath = learningPath;
        attempt.attemptTime = LocalDateTime.now();

        // Optional: Find the assessment
        if (taskAttemptDTO.assessmentId != null) {
            Assessment assessment = Assessment.findById(taskAttemptDTO.assessmentId);
            if (assessment != null) {
                attempt.assessment = assessment;
                memoryId = "evalTask-" + user.id + "-assess-" + assessment.id;
            }
        }

        // Optional: Find the learning step
        LearningStep learningStep = null;
        if (taskAttemptDTO.learningStepId != null) {
            learningStep = LearningStep.findById(taskAttemptDTO.learningStepId);
            if (learningStep != null) {
                attempt.learningStep = learningStep;
                memoryId = "evalTask-" + user.id + "-step-" + learningStep.id;
            }
        }
        Log.info("Attempt:" + attempt);

        String language = (learningPath.user.preferredLanguage != null) ? learningPath.user.preferredLanguage : "DE";
        String evaluationPrompt = createEvaluationPrompt(task, taskAttemptDTO.answer);
        String llmResponse = taskEvaluationService.evaluateTask(memoryId, evaluationPrompt, language);
        Log.info("LLM RES: " + llmResponse);

        // Parse the LLM response (assuming a JSON response structure)
        TaskEvaluationResponse evaluationResponse = parseLLMResponse(llmResponse);

        attempt.isCorrect = evaluationResponse.isCorrect;

        // Create a new TaskAnswer
        TaskAnswer answer = new TaskAnswer();
        answer.answer = taskAttemptDTO.answer;
        answer.attempt = attempt;
        answer.llmResponse = evaluationResponse.feedback;

        attempt.answer = answer;

        // Persist the attempt and the answer
        attempt.persist();
        answer.persist();

        UserTaskAttemptDTO response = new UserTaskAttemptDTO(attempt);
        return Response.ok(response).build();
    }




    @GET
    @Path("/user/{userId}")
    @Transactional
    public Response getUserAttempts(@PathParam("userId") Long userId) {
        List<UserTaskAttempt> attempts = UserTaskAttempt.list("user.id = ?1 ", userId);
        List<UserTaskAttemptDTO> response = attempts.stream()
                .map(UserTaskAttemptDTO::new)
                .collect(Collectors.toList());

//        Log.info(attempts);
//        Log.info(response);

        return Response.ok(response).build();
    }



    @GET
    @Path("/user/{userId}/learningpath/{learningPathId}/wrong-once")
    @Transactional
    public Response getTasksWrongOnce(@PathParam("userId") Long userId, @PathParam("learningPathId") Long learningPathId) {
        LearningPath learningPath = LearningPath.findById(learningPathId);
        if (learningPath == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Long assessmentId = learningPath.course.assessment.id;
        List<Long> learningStepIds = learningPath.learningSteps.stream()
                .map(step -> step.id)
                .collect(Collectors.toList());

        List<UserTaskAttempt> attempts = UserTaskAttempt.find(
                "user.id = ?1 AND isCorrect = false AND (assessment.id = ?2 OR learningStep.id IN ?3)",
                userId, assessmentId, learningStepIds
        ).list();

        List<TaskUserAttemptDTO> taskAttempts = attempts.stream()
                .collect(Collectors.groupingBy(UserTaskAttempt::getTask))
                .entrySet().stream()
                .filter(entry -> entry.getValue().size() == 1)
                .flatMap(entry -> entry.getValue().stream())
                .map(attempt -> new TaskUserAttemptDTO(attempt.getTask(), attempt))
                .collect(Collectors.toList());

        return Response.ok(taskAttempts).build();
    }

    @GET
    @Path("/user/{userId}/learningpath/{learningPathId}/wrong-multiple")
    @Transactional
    public Response getTasksWrongMultiple(@PathParam("userId") Long userId, @PathParam("learningPathId") Long learningPathId) {
        LearningPath learningPath = LearningPath.findById(learningPathId);
        if (learningPath == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Long assessmentId = learningPath.course.assessment.id;
        List<Long> learningStepIds = learningPath.learningSteps.stream()
                .map(step -> step.id)
                .collect(Collectors.toList());

        List<UserTaskAttempt> attempts = UserTaskAttempt.find(
                "user.id = ?1 AND isCorrect = false AND (assessment.id = ?2 OR learningStep.id IN ?3)",
                userId, assessmentId, learningStepIds
        ).list();

        Map<Task, List<UserTaskAttempt>> groupedAttempts = attempts.stream()
                .collect(Collectors.groupingBy(UserTaskAttempt::getTask));

        List<TaskUserAttemptsCountDTO> taskAttempts = groupedAttempts.entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .map(entry -> new TaskUserAttemptsCountDTO(entry.getKey(), entry.getValue(), entry.getValue().size()))
                .collect(Collectors.toList());

        return Response.ok(taskAttempts).build();
    }



/*
------------- HELPERS ------------
 */


    private String createEvaluationPrompt(Task task, String userAnswer) {
        return String.format("Task: %s\nUser's Solution: %s\nCorrect Answer: %s",
                task.question, userAnswer, task.correctAnswer);
    }

    private TaskEvaluationResponse parseLLMResponse(String llmResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(llmResponse, TaskEvaluationResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse LLM response", e);
        }
    }

}
