package de.htwg.learningpath.dto;

import de.htwg.course.dto.TaskDTO;
import de.htwg.course.dto.courses.get.LearningGoalGetDTO;
import de.htwg.learningpath.model.LearningStep;

import java.util.List;
import java.util.stream.Collectors;

public class LearningStepGetDTO {
    private Long id;
    private String readingMaterial;
    private String explanationText;
    private Long previousStepId;
    private Long nextStepId;

    private List<TaskDTO> tasks;

    // Constructor to map from LearningStep entity to LearningStepGetDTO
    public LearningStepGetDTO(LearningStep learningStep) {
        this.id = learningStep.id;
        this.readingMaterial = learningStep.readingMaterial;
        this.explanationText = learningStep.explanationText;
        this.tasks = learningStep.tasks.stream()
                .map(task -> new TaskDTO(task))
                .collect(Collectors.toList());
        this.previousStepId = learningStep.previousStep != null ? learningStep.previousStep.id : null;
        this.nextStepId = learningStep.nextStep != null ? learningStep.nextStep.id : null;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReadingMaterial() {
        return readingMaterial;
    }

    public void setReadingMaterial(String readingMaterial) {
        this.readingMaterial = readingMaterial;
    }

    public String getExplanationText() {
        return explanationText;
    }

    public void setExplanationText(String explanationText) {
        this.explanationText = explanationText;
    }

    public Long getPreviousStepId() {
        return previousStepId;
    }

    public void setPreviousStepId(Long previousStepId) {
        this.previousStepId = previousStepId;
    }

    public Long getNextStepId() {
        return nextStepId;
    }

    public void setNextStepId(Long nextStepId) {
        this.nextStepId = nextStepId;
    }

    public List<TaskDTO> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskDTO> tasks) {
        this.tasks = tasks;
    }
}

