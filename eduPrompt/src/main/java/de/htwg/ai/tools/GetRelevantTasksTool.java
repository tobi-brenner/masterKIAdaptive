package de.htwg.ai.tools;

import de.htwg.course.model.BloomLevel;
import de.htwg.course.model.Task;
import de.htwg.course.services.TaskService;
import dev.langchain4j.agent.tool.Tool;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class GetRelevantTasksTool {

    @Inject
    EntityManager entityManager;

    @Inject
    TaskService taskService;

    @Tool("Given the current topic and current bloom level, this tool will find tasks that are relevant for the learning step.")
    public String getRelevantTasks(Long userId, Long courseId, Long topicId, BloomLevel currentBloomLevel) {
        Log.info("userId: " + userId.toString());
        Log.info("courseId: " + courseId.toString());
        Log.info("topicId: " + topicId.toString());
        Log.info("BloomLevel: " + currentBloomLevel);
        // Determine the Bloom level range: current level +/- 1
        int currentLevel = currentBloomLevel.getLevel();
        BloomLevel minBloomLevel = BloomLevel.fromLevel(Math.max(currentLevel - 1, 1));
        BloomLevel maxBloomLevel = BloomLevel.fromLevel(Math.min(currentLevel + 1, 6));
        Log.info("current BloomLevel: " + currentLevel);
        Log.info("minBloom: " + minBloomLevel);
        Log.info("maxBloom: " + maxBloomLevel);

        List<Task> relevantTasks = Task.find("topic.id = ?1 and bloomLevel between ?2 and ?3",
                topicId, minBloomLevel, maxBloomLevel).list();

        Log.info("rewlevant tasks: " + relevantTasks);
        return relevantTasks.stream()
                .map(Task::toString)
                .collect(Collectors.joining(", ", "[", "]"));
    }
}
