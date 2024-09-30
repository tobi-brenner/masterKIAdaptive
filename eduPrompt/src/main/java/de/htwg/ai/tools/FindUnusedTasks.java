package de.htwg.ai.tools;


import de.htwg.course.model.BloomLevel;
import de.htwg.course.model.Task;
import de.htwg.course.services.TaskService;
import dev.langchain4j.agent.tool.Tool;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class FindUnusedTasks {

    @Inject
    TaskService taskService;

    @Tool("Find not attempted tasks for the course that the user has not attempted yet.")
    public String findUnusedTasks(Long courseId, Long userId, BloomLevel bloomLevel) {
        List<Task> unusedTasks = taskService.findUnusedTasks(courseId, userId, bloomLevel);
        StringBuilder result = new StringBuilder();
        result.append("[");
        for (Task task : unusedTasks) {
            result.append("{ \"id\": ").append(task.id).append(", \"question\": \"").append(task.question).append("\", \"bloomLevel\": \"").append(task.bloomLevel).append("\"},");
        }
        if (unusedTasks.size() > 0) {
            result.setLength(result.length() - 1); // remove last comma
        }
        result.append("]");
        return result.toString();
    }
}

