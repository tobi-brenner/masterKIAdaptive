package de.htwg.ai.tools;

import de.htwg.course.model.Task;
import de.htwg.course.services.TaskService;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class CheckExistingTasks {
    @Inject
    TaskService taskService;

    @Tool("Check which Tasks already exist in the course.")
    public String checkExistingTasks(Long courseId) {
        List<Task> courseTasks = taskService.getAllTasksForCourse(courseId);
        StringBuilder result = new StringBuilder();
        result.append("[");
        for (Task task : courseTasks) {
            result.append("{ \"id\": ").append(task.id).append(", \"question\": \"").append(task.question).append("\", \"verified\": ").append(task.verified).append("},");
        }
        if (courseTasks.size() > 0) {
            result.setLength(result.length() - 1); // remove last comma
        }
        result.append("]");
        return result.toString();
    }
}
