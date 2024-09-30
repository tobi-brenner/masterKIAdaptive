package de.htwg.ai.tools;

import de.htwg.course.model.Task;
import de.htwg.course.services.TaskService;
import dev.langchain4j.agent.tool.Tool;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class GetTasksOfTopic {
    @Inject
    TaskService taskService;

    @Tool("Check which Tasks already exist in the topic. Returns a list of all existing tasks for a topic. Must only be called once for each topic")
    public String getTasksOfTopic(Long topicId) {
        List<Task> topicTasks = taskService.getAllTasksForTopic(topicId);
        StringBuilder result = new StringBuilder();
        result.append("[");
        for (Task task : topicTasks) {
            result.append("{ \"id\": ").append(task.id).append(", \"question\": \"").append(task.question).append("\", \"verified\": ").append(task.verified).append("},");
        }
        if (topicTasks.size() > 0) {
            result.setLength(result.length() - 1); // remove last comma
        }
        result.append("]");
        Log.info(result);
        return result.toString();
    }
}
