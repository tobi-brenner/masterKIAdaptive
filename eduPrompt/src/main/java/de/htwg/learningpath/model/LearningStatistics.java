package de.htwg.learningpath.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.htwg.course.model.BloomLevel;
import de.htwg.course.model.Topic;
import de.htwg.user.model.User;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class LearningStatistics extends PanacheEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    public User user;

    @ManyToOne
    @JoinColumn(name = "learning_path_id")
    @JsonBackReference
    public LearningPath learningPath;

    @ManyToOne
    @JoinColumn(name = "learning_step_id", nullable = true)
    public LearningStep learningStep;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "topic_id")
    public Topic topic;

    @Column(length = 1024)
    public String strengths;

    @Column(length = 1024)
    public String weaknesses;

    @Column(length = 1024)
    public String recommendations;

    //    public int currentBloomLevel;
    @Enumerated(EnumType.STRING)
    public BloomLevel currentBloomLevel;

    public long timeSpent; // in seconds

    public LocalDateTime recordedAt;

    public LearningStatistics() {
        this.recordedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "LearningStatistics{" +
                "id=" + id +
                ", user=" + user.id +
                ", learningPath=" + learningPath.id +
                ", learningStep=" + (learningStep != null ? learningStep.id : "null") +
                ", topic=" + topic.id +
                ", strengths='" + strengths + '\'' +
                ", weaknesses='" + weaknesses + '\'' +
                ", recommendations='" + recommendations + '\'' +
                ", currentBloomLevel=" + currentBloomLevel +
                ", timeSpent=" + timeSpent +
                ", recordedAt=" + recordedAt +
                '}';
    }
}

