package de.htwg.course.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.htwg.course.model.Task;
import de.htwg.course.model.tasktype.DragAndDropTask;
import de.htwg.course.model.tasktype.MatchingTask;
import de.htwg.course.model.tasktype.MultipleChoiceTask;
import de.htwg.course.model.tasktype.SortingTask;

import java.io.IOException;
import java.util.Map;

public class TaskCreateDTO {
    public Long topicId;
    public Long assessmentId;
    public Long id;
    public String question;
    public String bloomLevel;
    //    public Map<String, Object> correctAnswer;
    public String correctAnswer;
    public String taskType;
    public Map<String, String> options;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public TaskCreateDTO(Task task) {
        this.id = task.id;
        this.question = task.question;
        this.bloomLevel = task.bloomLevel.toString();
        this.correctAnswer = task.correctAnswer;
        this.taskType = task.taskType;
        this.options = fetchOptionsIfAvailable(task);
    }

    private Map<String, Object> parseJsonCorrectAnswer(String json) {
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse correctAnswer JSON", e);
        }
    }

    private Map<String, String> fetchOptionsIfAvailable(Task task) {
        if (task instanceof MultipleChoiceTask) {
            return ((MultipleChoiceTask) task).getOptions();
        } else if (task instanceof DragAndDropTask) {
            return ((DragAndDropTask) task).getOptions();
        } else if (task instanceof SortingTask) {
            return ((SortingTask) task).getOptions();
        } else if (task instanceof MatchingTask) {
            return ((MatchingTask) task).getOptions();
        }
        return null; // Return null if no options are available
    }
}
