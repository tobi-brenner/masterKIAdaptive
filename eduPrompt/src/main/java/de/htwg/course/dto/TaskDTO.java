package de.htwg.course.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.htwg.course.dto.courses.get.TopicGetDTO;
import de.htwg.course.model.Task;
import de.htwg.course.model.tasktype.DragAndDropTask;
import de.htwg.course.model.tasktype.MatchingTask;
import de.htwg.course.model.tasktype.MultipleChoiceTask;
import de.htwg.course.model.tasktype.SortingTask;

import java.io.IOException;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskDTO {
    public Long id;
    public String question;
    public String hint;
    public String bloomLevel;
    public TopicGetDTO topic;
    public Long topicId;
    //    public Map<String, Object> correctAnswer;
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "taskType", visible = true)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = String.class, name = "FREE_TEXT"),
            @JsonSubTypes.Type(value = String.class, name = "SHORT_ANSWER"),
            @JsonSubTypes.Type(value = String.class, name = "CODE_COMPLETION"),
            @JsonSubTypes.Type(value = String.class, name = "ESSAY"),
            @JsonSubTypes.Type(value = Map.class, name = "MULTIPLE_CHOICE"),
            @JsonSubTypes.Type(value = Map.class, name = "TRUE_FALSE"),
            @JsonSubTypes.Type(value = Map.class, name = "MATCHING"),
            @JsonSubTypes.Type(value = Map.class, name = "SORTING"),
            @JsonSubTypes.Type(value = Map.class, name = "DRAG_AND_DROP"),
            @JsonSubTypes.Type(value = Map.class, name = "FILL_IN_THE_BLANKS")
    })
    public Object correctAnswer;
    //    public String correctAnswer;
    public String taskType;
    public Map<String, String> options;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public TaskDTO() {
    }

    public TaskDTO(Task task) {
        this.id = task.id;
        this.question = task.question;
        this.bloomLevel = task.bloomLevel.toString();
        this.correctAnswer = task.correctAnswer;
//        this.correctAnswer = parseJsonCorrectAnswer(task.correctAnswer);
        this.taskType = task.taskType;
        this.options = fetchOptionsIfAvailable(task);
        if (task.hint != null) {
            this.hint = task.hint;
        }
        this.topicId = task.topic != null ? task.topic.id : null;
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
        return null;
    }
}
