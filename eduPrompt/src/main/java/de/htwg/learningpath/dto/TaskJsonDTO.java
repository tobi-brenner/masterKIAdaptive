package de.htwg.learningpath.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Map;


public class TaskJsonDTO {
    @JsonProperty("bloomLevel")
    private String bloomLevel;

    @JsonProperty("taskType")
    private String taskType;

    @JsonProperty("question")
    private String question;
    @JsonProperty("hint")
    private String hint;

    @JsonProperty("correctAnswer")
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
    private Object correctAnswer;

    @JsonProperty("options")
    private Map<String, String> options;

    // Getters and setters

    public String getBloomLevel() {
        return bloomLevel;
    }

    public void setBloomLevel(String bloomLevel) {
        this.bloomLevel = bloomLevel;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Object getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(Object correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }
}

