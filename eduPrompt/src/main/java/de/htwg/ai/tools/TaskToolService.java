package de.htwg.ai.tools;

import de.htwg.ai.services.CompareTasksService;
import de.htwg.course.model.BloomLevel;
import de.htwg.course.model.Task;
import de.htwg.course.services.TaskService;
import de.htwg.learningpath.model.UserTaskAttempt;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class TaskToolService {

    @Inject
    EntityManager entityManager;

    @Inject
    TaskService taskService;

    @Inject
    CompareTasksService compareTasksService;


    @Tool("Compares new Tasks with existing Tasks. This tool will find the existing Tasks by the course Id and compares them with the new Tasks. ")
    public String compareNewWithExistingTasks(Long courseId, String newTasks) {
        List<Task> courseTasks = taskService.getAllTasksForCourse(courseId);
        String existingTasks = courseTasks.stream().map(task -> String.format("ID: %d, Question: %s, Verified: %s", task.id, task.question, task.verified)).collect(Collectors.joining("\n"));
        return compareTasksService.compare(newTasks, existingTasks);
    }

}
