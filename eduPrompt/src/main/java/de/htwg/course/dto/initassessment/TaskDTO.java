package de.htwg.course.dto.initassessment;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.gson.Gson;
import de.htwg.course.dto.initassessment.deserializer.CorrectAnswerDeserializer;

import java.util.Map;

public class TaskDTO {
    private String bloomLevel;
    private String taskType;
    private String question;
    private Map<String, String> options; // Assuming options are structured as a map
    private Map<String, String> correctAnswer;

    private String language;

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

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    public Map<String, String> getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(Map<String, String> correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
