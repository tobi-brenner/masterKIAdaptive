package de.htwg.ai.tools;

import de.htwg.course.model.BloomLevel;
import de.htwg.course.model.Task;
import de.htwg.learningpath.model.UserTaskAttempt;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class GetTasksByAttemptTool {

    @Tool("Fetches tasks for a user and categorizes them into not done, successfully completed, and wrong attempts.")
    public String getUserTaskAttemptsSummary(Long userId, Long courseId, Long topicId, BloomLevel currentBloomLevel) {
        // Fetch all tasks for the given topic and Bloom level range
        int currentLevel = currentBloomLevel.getLevel();
        BloomLevel minBloomLevel = BloomLevel.fromLevel(Math.max(currentLevel - 1, 1));
        BloomLevel maxBloomLevel = BloomLevel.fromLevel(Math.min(currentLevel + 1, 6));

        List<Task> allTasks = Task.find("topic.id = ?1 and bloomLevel between ?2 and ?3",
                topicId, minBloomLevel, maxBloomLevel).list();

        // Fetch user task attempts
        List<UserTaskAttempt> userAttempts = UserTaskAttempt.find("user.id = ?1 and task.topic.id = ?2",
                userId, topicId).list();

        // Create a map for easy lookup of task attempts
        Map<Long, UserTaskAttempt> attemptsMap = userAttempts.stream()
                .collect(Collectors.toMap(attempt -> attempt.task.id, attempt -> attempt));

        // Categorize tasks
        List<Task> notDone = new ArrayList<>();
        List<Task> successfullyCompleted = new ArrayList<>();
        List<Task> wrongAttempts = new ArrayList<>();

        for (Task task : allTasks) {
            UserTaskAttempt attempt = attemptsMap.get(task.id);
            if (attempt == null) {
                notDone.add(task);
            } else if (attempt.isCorrect) {
                successfullyCompleted.add(task);
            } else {
                wrongAttempts.add(task);
            }
        }

        // Format lists into a string
        String result = "Not Done: " + notDone.stream().map(Task::toString).collect(Collectors.joining(", ", "[", "]")) +
                "\nSuccessfully Completed: " + successfullyCompleted.stream().map(Task::toString).collect(Collectors.joining(", ", "[", "]")) +
                "\nWrong Attempts: " + wrongAttempts.stream().map(Task::toString).collect(Collectors.joining(", ", "[", "]"));

        return result;
    }
}
