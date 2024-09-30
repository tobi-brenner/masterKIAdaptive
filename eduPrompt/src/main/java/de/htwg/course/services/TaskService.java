package de.htwg.course.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.htwg.course.dto.GeneratedTasksResponseDTO;
import de.htwg.course.dto.TaskCreateDTO;
import de.htwg.course.model.*;
import de.htwg.course.dto.TaskDTO;
import de.htwg.course.model.tasktype.*;
import de.htwg.course.strategy.AnswerCheckStrategy;
import de.htwg.course.strategy.StrategyFactory;
import de.htwg.learningpath.model.LearningPath;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;

@ApplicationScoped
public class TaskService {
    @Inject
    StrategyFactory strategyFactory;

    @Inject
    EntityManager entityManager;

    @Inject
    ObjectMapper objectMapper;

    @Transactional
    public List<Task> findUnusedTasks(Long courseId, Long userId, BloomLevel userBloomLevel) {
        Log.info("BLOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOM: -_->" + userBloomLevel);
        String jpql = "SELECT t FROM Task t " +
                "JOIN t.topic tp " +
                "JOIN tp.course c " +
                "LEFT JOIN UserTaskAttempt uta ON uta.task.id = t.id AND uta.user.id = :userId " +
                "WHERE c.id = :courseId AND uta.id IS NULL AND t.bloomLevel >= :userBloomLevel";


        TypedQuery<Task> query = entityManager.createQuery(jpql, Task.class);
        query.setParameter("courseId", courseId);
        query.setParameter("userId", userId);
        query.setParameter("userBloomLevel", userBloomLevel);

        return query.getResultList();
    }


    @Transactional
    public TaskEvaluationResult evaluateAnswer(Task task, String answer) {
        AnswerCheckStrategy strategy = strategyFactory.getStrategy(task.getClass().getSimpleName());
        return strategy.checkAnswer(answer, task.correctAnswer);
    }

    @Transactional
    public Task createTask(TaskCreateDTO taskDTO) {
        Assessment assessment = Assessment.findById(taskDTO.assessmentId);
        Topic topic = Topic.findById(taskDTO.topicId);
        Task task = createTaskFromDTO(taskDTO);
        task.question = taskDTO.question;
        task.bloomLevel = BloomLevel.valueOf(taskDTO.bloomLevel);
        task.correctAnswer = taskDTO.correctAnswer;
        task.taskType = taskDTO.taskType;
        task.persist();
        return task;
    }


    @Transactional
    public Task updateTask(Long id, TaskDTO taskDTO) {
        Task task = Task.findById(id);
        if (task == null) {
            return null;
        }
        task.question = taskDTO.question;
        task.bloomLevel = BloomLevel.valueOf(taskDTO.bloomLevel);
        task.correctAnswer = taskDTO.correctAnswer.toString();
        task.taskType = taskDTO.taskType;
        // Add other fields and relationships as needed
        task.persist();
        return task;
    }

    @Transactional
    public boolean deleteTask(Long id) {
        Task task = Task.findById(id);
        if (task == null) {
            return false;
        }
        Task.deleteById(id);
//        task.delete();
        return true;
    }

    public Task getTask(Long id) {
        return Task.findById(id);
    }

    public List<Task> getAllTasks() {
        return Task.listAll();
    }

    @Transactional
    public List<Task> getAllTasksForCourse(Long courseId) {
        // Fetch the course to ensure it exists
        Course course = entityManager.find(Course.class, courseId);
        if (course == null) {
            throw new IllegalArgumentException("Course with ID " + courseId + " not found.");
        }

        // Query to get all tasks for the course
        TypedQuery<Task> query = entityManager.createQuery(
                "SELECT t FROM Task t WHERE t.topic.course.id = :courseId", Task.class);
        query.setParameter("courseId", courseId);
        return query.getResultList();
    }

    @Transactional
    public List<Task> getAllTasksForTopic(Long topicId) {
        // Fetch the course to ensure it exists
        Topic topic = entityManager.find(Topic.class, topicId);
        if (topic == null) {
            throw new IllegalArgumentException("Topic with ID " + topicId + " not found.");
        }

        // Query to get all tasks for the course
        TypedQuery<Task> query = entityManager.createQuery(
                "SELECT t FROM Task t WHERE t.topic.id = :topicId", Task.class);
        query.setParameter("topicId", topicId);
        return query.getResultList();
    }

    @Transactional
    public List<Task> findIncorrectTasks(Long topicId, Long userId) {
        return entityManager.createQuery("SELECT t FROM Task t JOIN UserTaskAttempt uta ON t.id = uta.task.id WHERE t.topic.id = :topicId AND uta.user.id = :userId AND uta.isCorrect = false", Task.class)
                .setParameter("topicId", topicId)
                .setParameter("userId", userId)
                .getResultList();
    }


    private Task createTaskFromDTO(TaskCreateDTO taskDTO) {
        // TODO: adjust this witch TaskFactory
        // Create different types of tasks based on the type
        Task task;
        switch (taskDTO.taskType.toUpperCase()) {
            case "MULTIPLE_CHOICE":
                MultipleChoiceTask multipleChoiceTask = new MultipleChoiceTask();
                multipleChoiceTask.setOptions(taskDTO.options); // Assuming JSON handling is encapsulated
                task = multipleChoiceTask;
                break;
            case "MATCHING":
                MatchingTask matchingTask = new MatchingTask();
                matchingTask.setOptions(taskDTO.options); // Assuming JSON handling is encapsulated
                task = matchingTask;
                break;
            case "SORTING":
                SortingTask sortingTask = new SortingTask();
                sortingTask.setOptions(taskDTO.options); // Assuming JSON handling is encapsulated
                task = sortingTask;
                break;
            case "DRAG_AND_DROP":
                DragAndDropTask dragAndDropTask = new DragAndDropTask();
                dragAndDropTask.setOptions(taskDTO.options); // Assuming JSON handling is encapsulated
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
                throw new IllegalArgumentException("Unsupported task type: " + taskDTO.taskType);
        }
        task.question = taskDTO.question;
        task.correctAnswer = taskDTO.correctAnswer.toString();
        task.bloomLevel = BloomLevel.valueOf(taskDTO.bloomLevel);
        return task;
    }


    public GeneratedTasksResponseDTO parseGeneratedTasks(String jsonString) throws Exception {
        return objectMapper.readValue(jsonString, GeneratedTasksResponseDTO.class);
    }
}
