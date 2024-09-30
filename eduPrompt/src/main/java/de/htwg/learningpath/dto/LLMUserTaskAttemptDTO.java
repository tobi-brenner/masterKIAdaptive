package de.htwg.learningpath.dto;

public class LLMUserTaskAttemptDTO {
    public Long taskId;
    public boolean isCorrect;
    public String answer;
    public String feedback;

    public LLMUserTaskAttemptDTO(Long taskId, boolean isCorrect, String answer, String feedback) {
        this.taskId = taskId;
        this.isCorrect = isCorrect;
        this.answer = answer;
        this.feedback = feedback;
    }

    @Override
    public String toString() {
        return "LLMUserTaskAttemptDTO{" +
                "taskId=" + taskId +
                ", isCorrect=" + isCorrect +
                ", answer='" + answer + '\'' +
                ", feedback='" + feedback + '\'' +
                '}';
    }
}

