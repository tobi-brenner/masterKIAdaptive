package de.htwg.course.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Assessment extends PanacheEntity {
    @Column(name = "is_initial", nullable = false)
    public Boolean isInitial;

    @Column(name = "is_skipped", nullable = true)
    public Boolean isSkipped;

    @Column(columnDefinition = "TEXT")
    public String description;

    @OneToOne
    @JoinColumn(name = "course_id")
    @JsonBackReference(value = "course-assessment")
    public Course course;

    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference(value = "assessment-tasks")
    public List<Task> tasks;


//    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL, orphanRemoval = true)
//    public List<Question> questions;
}
