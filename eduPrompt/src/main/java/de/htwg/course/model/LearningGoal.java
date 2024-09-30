package de.htwg.course.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

@Entity
public class LearningGoal extends PanacheEntity {

    public String goal;
    @Column(length = 500)
    public String description;
    @Column(name = "max_bloom", nullable = true)
    public BloomLevel maxBloom;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    @JsonBackReference(value = "topic-learningGoals")
    public Topic topic;

    @Override
    public String toString() {
        return "LearningGoal{" +
                "goal='" + goal + '\'' +
                "description='" + description + '\'' +
                '}';
    }

}
