package de.htwg.learningpath.model;

import de.htwg.user.model.User;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "feedback")
public class Feedback extends PanacheEntity {
    @ManyToOne
    @JoinColumn(name = "learning_path_id")
    public LearningPath learningPath;

    @ManyToOne
    @JoinColumn(name = "prof_id")
    public User professor;

    @Column(nullable = false, length = 1024)
    public String content;

    @Column(nullable = false)
    public Date createdAt;
    @Column(nullable = false)
    public FeedbackType feedbackType;

    @Column(nullable = false)
    public boolean isIngestedToLLM = false;

    public enum FeedbackType {
        DIRECT,
        LLM
    }
}