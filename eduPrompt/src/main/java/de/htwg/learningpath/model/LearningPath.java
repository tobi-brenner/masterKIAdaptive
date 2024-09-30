package de.htwg.learningpath.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.htwg.course.model.Course;
import de.htwg.user.model.User;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class LearningPath extends PanacheEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    public User user;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonBackReference(value = "course-learningpath")
    public Course course;

    @Nullable
    public boolean initialAssessmentCompleted;

    @OneToMany(mappedBy = "learningPath", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    public List<LearningStep> learningSteps;

    public String initialEvaluation;

    @JsonIgnore
    @OneToMany(mappedBy = "learningPath")
    public List<Feedback> feedbacks;

    @JsonIgnore
    @OneToMany(mappedBy = "learningPath")
    public List<Message> messages;

    @OneToMany(mappedBy = "learningPath", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    public List<LearningStatistics> learningStatistics;


    @Override
    public String toString() {
        return "LearningPath{" +
                "id=" + id +
                ", userId=" + (user != null ? user.id : null) +
                ", courseId=" + (course != null ? course.id : null) +
                ", initialAssessmentCompleted=" + initialAssessmentCompleted +
                ", initialEvaluation='" + initialEvaluation + '\'' +
                ", learningSteps=" + (learningSteps != null ? learningSteps.stream().map(LearningStep::getId).collect(Collectors.toList()) : null) +
                '}';
    }


}

