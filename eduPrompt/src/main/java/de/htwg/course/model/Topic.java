package de.htwg.course.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Topic extends PanacheEntity {

    public String name;
    @Column(length = 2048)
    public String description;
    public int orderNumber;
    @Column(name = "max_bloom", nullable = true)
    public BloomLevel maxBloom;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnore
    @JsonBackReference(value = "course-topics")
    public Course course;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference(value = "topic-learningGoals")
    public List<LearningGoal> learningGoals;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference(value = "topic-tasks")
    public List<Task> tasks;

    @Override
    public String toString() {
        return "Topic{" +
                "id='" + id + '\'' +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", orderNumber='" + orderNumber + '\'' +
                ", learningGoals=" + learningGoals.stream()
                .map(LearningGoal::toString)
                .collect(Collectors.joining(", ")) +
                '}';
    }
}
