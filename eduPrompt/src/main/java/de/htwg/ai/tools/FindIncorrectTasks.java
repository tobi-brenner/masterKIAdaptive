package de.htwg.ai.tools;

import de.htwg.course.model.Task;
import de.htwg.course.services.TaskService;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class FindIncorrectTasks {

    @Inject
    TaskService taskService;

    @Tool("Find tasks for the topic that the user has answered incorrectly.")
    public String findIncorrectTasks(Long topicId, Long userId) {
        List<Task> incorrectTasks = taskService.findIncorrectTasks(topicId, userId);
        StringBuilder result = new StringBuilder();
        result.append("[");
        for (Task task : incorrectTasks) {
            result.append("{ \"id\": ").append(task.id)
                    .append(", \"question\": \"").append(task.question)
                    .append("\", \"bloomLevel\": \"").append(task.bloomLevel)
                    .append("\"},");
        }
        if (incorrectTasks.size() > 0) {
            result.setLength(result.length() - 1); // remove last comma
        }
        result.append("]");
        return result.toString();
    }
}