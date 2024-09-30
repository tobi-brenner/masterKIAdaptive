package de.htwg.learningpath.dto;

public class TaskAttemptDTO {
    public Long userId;
    public Long taskId;
    public String answer;
    public Long assessmentId; // Optional
    public Long learningStepId; // Optional
    public Long learningPathId;
    public boolean isCorrect;


}
