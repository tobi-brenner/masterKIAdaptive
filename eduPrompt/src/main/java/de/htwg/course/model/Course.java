package de.htwg.course.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.htwg.learningpath.model.LearningPath;
import de.htwg.user.model.User;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.util.*;

@Entity
public class Course extends PanacheEntity {

    @Column(name = "subject", nullable = false, length = 500)
    public String subject;

    @Column(name = "description", length = 1000)
    public String description;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "course-topics")
    public List<Topic> topics = new ArrayList<>();

    @OneToOne(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "course-assessment")
    public Assessment assessment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    @JoinColumn(name = "professor_id")
    public User prof;

    @OneToOne(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "course-settings")
    public CourseSettings courseSettings;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference(value = "course-learningpath")
    public Set<LearningPath> learningPaths;

    @OneToOne(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "course-material")
    public CourseMaterial courseMaterial;

    @ManyToMany(mappedBy = "courses", fetch = FetchType.LAZY)
    @JsonBackReference(value = "course-users")
    public Set<User> users = new HashSet<>();

    @Override
    public String toString() {
        return "Course{" +
                "subject='" + subject + '\'' +
                ", description='" + description + '\'' +
                ", topics=" + topics +
                ", assessment=" + assessment +
                ", prof='" + prof + '\'' +
                ", courseMaterial=" + courseMaterial +
                ", courseSettings=" + courseSettings +
                ", users=" + users +
                '}';
    }
}
