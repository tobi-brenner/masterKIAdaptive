package de.htwg.course.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
//import de.htwg.course.dto.TaskDTO;
//import de.htwg.course.dto.AssessmentDTO;
import de.htwg.course.dto.initassessment.AssessmentDTO;
import de.htwg.course.dto.initassessment.SelectedTasksDTO;
import de.htwg.course.dto.initassessment.TaskDTO;
import de.htwg.course.dto.initassessment.TopicDTO;
import de.htwg.course.model.*;
import de.htwg.course.model.tasktype.*;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class AssessmentService {
    @Inject
    EntityManager entityManager;


    @Transactional
    public void createAssessmentFromJson(String json, Course course) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            AssessmentDTO assessmentDTO = mapper.readValue(json, new TypeReference<AssessmentDTO>() {
            });
            Assessment assessment;
            if (course.assessment != null) {
                assessment = entityManager.find(Assessment.class, course.assessment.id);
                if (assessment == null) {
                    assessment = new Assessment();
                    assessment.description = "Initial assessment based on AI recommendations";
                    assessment.isInitial = true;
                    assessment.course = course;
                    entityManager.persist(assessment);
                    course.assessment = assessment;
                }
            } else {
                assessment = new Assessment();
                assessment.description = "Initial assessment based on AI recommendations";
                assessment.isInitial = true;
                assessment.course = course;
                entityManager.persist(assessment);
                course.assessment = assessment;
            }
            Log.info("Assessment ID: " + assessment.id);

            entityManager.merge(course);

            for (TopicDTO topicDTO : assessmentDTO.topics) {
                Topic topic = entityManager.find(Topic.class, topicDTO.getTopicId());
                if (topic != null) {
                    for (TaskDTO taskDTO : topicDTO.getTasks()) {
                        Task task = createTask(taskDTO);
                        task.topic = topic;
                        task.assessment = assessment;
                        entityManager.persist(task);
                    }
                }
            }

        } catch (Exception e) {
            Log.info(e);
            throw e;

        }
    }


    @Transactional
    public void createAssessmentFromSelectedTasks(String json, Course course) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            // Parse the new simplified response
            SelectedTasksDTO selectedTasksDTO = mapper.readValue(json, SelectedTasksDTO.class);

            Assessment assessment;
            if (course.assessment != null) {
                assessment = entityManager.find(Assessment.class, course.assessment.id);
            } else {
                assessment = new Assessment();
                assessment.description = "Initial assessment based on AI recommendations";
                assessment.isInitial = true;
                assessment.course = course;
                entityManager.persist(assessment);
                course.assessment = assessment;
            }
            Log.info("ASSESSMENT");
            Log.info(assessment);
            Log.info("COURSE ASSESSMENT");
            Log.info(course.assessment);
            entityManager.merge(course);


            List<Task> selectedTasks = new ArrayList<>();
            for (SelectedTasksDTO.TopicTasksDTO topicTasks : selectedTasksDTO.topics) {
                for (Long taskId : topicTasks.selectedTaskIds) {
                    Task task = entityManager.find(Task.class, taskId);
                    Log.info("TASK");
                    Log.info(task);
                    if (task != null) {
                        task.assessment = assessment;
                        entityManager.merge(task);
                        selectedTasks.add(task);
                        Log.info(task);
                    }
                }
            }

            assessment.tasks = selectedTasks;
            entityManager.merge(assessment);
//            entityManager.merge(course);

        } catch (Exception e) {
            Log.error("Error creating initial assessment: ", e);
            throw e;
        }
    }


    private Task createTask(TaskDTO taskDTO) {
        // TODO: adjust this with TaskFactory
        // Create different types of tasks based on the type
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
        task.correctAnswer = convertCorrectAnswer(taskDTO.getCorrectAnswer());
        task.bloomLevel = BloomLevel.valueOf(taskDTO.getBloomLevel());
        return task;
    }

    private String convertCorrectAnswer(Object correctAnswer) {
        if (correctAnswer instanceof String) {
            return (String) correctAnswer;
        } else if (correctAnswer instanceof Map) {
            return correctAnswer.toString();
        } else {
            throw new IllegalArgumentException("Unsupported correctAnswer type: " + correctAnswer.getClass().getName());
        }
    }
}
