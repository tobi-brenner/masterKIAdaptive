package de.htwg.learningpath.dto;

import de.htwg.learningpath.model.Feedback;

import java.util.Date;

public class FeedbackDTO {

    public Long id;
    public Date createdAt;

    public Long learningPathId;
    public Long professorId;
    public String content;
    public Feedback.FeedbackType feedbackType;
    public boolean isIngestedToLLM;

    public FeedbackDTO() {
    }

    public FeedbackDTO(Feedback feedback) {
        this.id = feedback.id;
        this.learningPathId = feedback.learningPath.id;
        this.professorId = feedback.professor.id;
        this.content = feedback.content;
        this.createdAt = feedback.createdAt;
        this.feedbackType = feedback.feedbackType;
        this.isIngestedToLLM = feedback.isIngestedToLLM;
    }
}

