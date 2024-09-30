package de.htwg.learningpath.services;

import de.htwg.course.model.BloomLevel;
import de.htwg.course.model.Task;
import de.htwg.course.model.Topic;
import de.htwg.learningpath.dto.LLMUserTaskAttemptDTO;
import de.htwg.learningpath.model.LearningPath;
import de.htwg.learningpath.model.LearningStatistics;
import de.htwg.learningpath.model.UserTaskAttempt;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for handling LearningPath related operations.
 * Provides methods to manage LearningPaths, fetch task attempts, and generate task completion summaries.
 */
@ApplicationScoped
public class LearningPathService {

    /**
     * Finds learning paths associated with a specific user.
     *
     * @param userId The ID of the user.
     * @return A list of learning paths associated with the user.
     */
    public List<LearningPath> findLearningPathsByUserId(Long userId) {
        return LearningPath.list("user.id", userId);
    }


    /**
     * Generates a summary string of the user's task completion status for each topic and Bloom level
     * within a given learning path. The summary includes the number of correct attempts out of the total
     * number of tasks for each Bloom level and topic.
     *
     * @param userId         The ID of the user.
     * @param learningPathId The ID of the learning path.
     * @param courseId       The ID of the course associated with the learning path.
     * @return A formatted string detailing the user's task completion progress by topic and Bloom level.
     */
    public String generateTaskCompletionString(Long userId, Long learningPathId, Long courseId) {
        // Fetch all topics for the learning path
        List<Topic> topics = Topic.list("course.id", courseId);
        Log.info(topics);

        // Fetch all task attempts by user for the learning path
        List<UserTaskAttempt> attempts = UserTaskAttempt.list("user.id = ?1 and learningPath.id = ?2", userId, learningPathId);
        Log.info(attempts);

        // Group correct attempts by topic and bloom level
        Map<Long, Map<BloomLevel, Long>> correctAttemptsByTopicAndBloom = attempts.stream()
                .filter(UserTaskAttempt::isCorrect) // Only count correct attempts
                .collect(Collectors.groupingBy(
                        attempt -> attempt.task.topic.id,
                        Collectors.groupingBy(
                                attempt -> attempt.task.bloomLevel,
                                Collectors.counting()
                        )
                ));

        // Build the task completion string
        StringBuilder completionString = new StringBuilder();

        for (Topic topic : topics) {
            Long topicId = topic.id;
            completionString.append("Topic ").append(topic.name).append(":\n");

            // Group tasks by bloom level
            Map<BloomLevel, List<Task>> tasksByBloomLevel = topic.tasks.stream()
                    .collect(Collectors.groupingBy(task -> task.bloomLevel));

            for (Map.Entry<BloomLevel, List<Task>> entry : tasksByBloomLevel.entrySet()) {
                BloomLevel bloomLevel = entry.getKey();
                int totalCount = entry.getValue().size();

                // Get the count of correct attempts for this topic and bloom level
                long correctCount = correctAttemptsByTopicAndBloom.getOrDefault(topicId, new EnumMap<>(BloomLevel.class))
                        .getOrDefault(bloomLevel, 0L);

                if (totalCount > 0) { // Only include levels with tasks
                    completionString.append(bloomLevel.name())
                            .append(" ")
                            .append(correctCount)
                            .append("/")
                            .append(totalCount)
                            .append("\n");
                }
            }
        }

        return completionString.toString();
    }

    /**
     * Determines which topics should be part of the next learning step, based on the user's progress
     * in the current learning path. This method evaluates each topic based on the user's current Bloom level
     * and the maximum Bloom level for each topic.
     *
     * @param topics             The list of topics in the course.
     * @param learningStatistics The list of learning statistics associated with the user's progress.
     * @return A list of topics to be included in the next learning step.
     */
    public List<Topic> getTopicsForLearningStep(List<Topic> topics, List<LearningStatistics> learningStatistics) {
        Log.info(topics);
        Log.info(learningStatistics);
        List<Topic> sortedTopics = topics.stream()
                .sorted(Comparator.comparingInt((Topic t) -> t.orderNumber != 0 ? t.orderNumber : t.id.intValue()))
                .collect(Collectors.toList());

        Map<Long, LearningStatistics> statisticsMap = learningStatistics.stream()
                .collect(Collectors.toMap(stat -> stat.topic.id, stat -> stat));

        // Evaluate each topic based on Bloom levels
        List<Topic> result = new ArrayList<>();
        for (int i = 0; i < sortedTopics.size(); i++) {
            Topic topic = sortedTopics.get(i);
            LearningStatistics stat = statisticsMap.get(topic.id);

            if (stat != null) {
                BloomLevel currentBloom = stat.currentBloomLevel;
                BloomLevel maxBloom = topic.maxBloom;

                if (currentBloom.ordinal() < maxBloom.ordinal()) {
                    result.add(topic);

                    if (maxBloom.ordinal() - currentBloom.ordinal() == 1 && i + 1 < sortedTopics.size()) {
                        Topic nextTopic = sortedTopics.get(i + 1);
                        result.add(nextTopic);
                    }
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Fetch the user's task attempts for their learning path and convert them into a formatted string.
     *
     * @param userId         The ID of the user
     * @param learningPathId The ID of the learning path
     * @return A formatted string containing the user's task attempts
     */
    public String getUserTaskAttemptsAsString(Long userId, Long learningPathId) {
        // Fetch all task attempts by the user for the specified learning path
        List<UserTaskAttempt> attempts = UserTaskAttempt.list("user.id = ?1 and learningPath.id = ?2", userId, learningPathId);

        // Log the fetched attempts
        Log.info("Fetched UserTaskAttempts for User ID: " + userId + " and LearningPath ID: " + learningPathId);
        Log.info(attempts);

        // Check if there are no attempts
        if (attempts.isEmpty()) {
            return "No task attempts found for the user in this learning path.";
        }

        // Build a string representation of the task attempts
        String taskAttemptsString = attempts.stream()
                .map(attempt -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Task ID: ").append(attempt.task.id)
                            .append(", Attempt Time: ").append(attempt.attemptTime)
                            .append(", Correct: ").append(attempt.isCorrect ? "Yes" : "No")
                            .append(", Assessment ID: ").append(attempt.assessment != null ? attempt.assessment.id : "None")
                            .append(", Learning Step ID: ").append(attempt.learningStep != null ? attempt.learningStep.id : "None")
                            .append(", Task Answer: ").append(attempt.answer != null ? attempt.answer.answer : "None")
                            .append(", Feedback: ").append(attempt.answer != null ? attempt.answer.llmResponse : "None");
                    return sb.toString();
                })
                .collect(Collectors.joining("\n"));

        return taskAttemptsString;
    }

    /**
     * This method evaluates the current topic based on learning statistics and checks whether the next topic should be included.
     * If the gap between the current topic's BloomLevel and its maxBloomLevel is small (difference of 1), both the current and next topics are returned.
     *
     * @param topics             A list of topics in the course
     * @param learningStatistics A list of LearningStatistics for the user
     * @return An array of topics, which may include the current and the next topic based on their bloom levels
     */
    public List<Topic> determineCurrentAndNextTopic(List<Topic> topics, List<LearningStatistics> learningStatistics) {
        // Sort topics by orderNumber to ensure correct sequential order
        List<Topic> sortedTopics = topics.stream()
                .sorted(Comparator.comparingInt(t -> t.orderNumber))
                .collect(Collectors.toList());

        // Map statistics by topic ID for quick lookup
        Map<Long, LearningStatistics> statsByTopicId = learningStatistics.stream()
                .collect(Collectors.toMap(stat -> stat.topic.id, stat -> stat));

        List<Topic> result = new ArrayList<>();

        // Iterate through topics to find the current topic that is not yet finished
        for (int i = 0; i < sortedTopics.size(); i++) {
            Topic currentTopic = sortedTopics.get(i);
            LearningStatistics currentStat = statsByTopicId.get(currentTopic.id);

            // If there's no stat for the current topic, the user hasn't started it, so this is the current topic
            if (currentStat == null || currentStat.currentBloomLevel.getLevel() < currentTopic.maxBloom.getLevel()) {
                result.add(currentTopic);

                // Check if the gap between currentBloomLevel and maxBloomLevel is small (difference of 1)
                if (currentStat != null && currentTopic.maxBloom.getLevel() - currentStat.currentBloomLevel.getLevel() == 1) {
                    // Add the next topic if there is one
                    if (i + 1 < sortedTopics.size()) {
                        Topic nextTopic = sortedTopics.get(i + 1);
                        result.add(nextTopic);
                    }
                }

                // Break after finding the current topic (and possibly the next)
                break;
            }
        }

        return result;
    }

    /**
     * This method converts a UserTaskAttempt to a LLMUserTaskAttempt
     *
     * @param attempt A list of topics in the course
     * @return LLMUserTaskAttemptDTO
     */
    public LLMUserTaskAttemptDTO toLLMDTO(UserTaskAttempt attempt) {
        return new LLMUserTaskAttemptDTO(
                attempt.task != null ? attempt.task.id : null, // taskId
                attempt.isCorrect,                             // isCorrect
                attempt.answer != null ? attempt.answer.answer : "null", // answer
                attempt.answer != null ? attempt.answer.llmResponse : "null" // feedback
        );
    }
}