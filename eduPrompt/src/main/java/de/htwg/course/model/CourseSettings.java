package de.htwg.course.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.temporal.ChronoUnit;
import java.util.Date;


@Entity
public class CourseSettings extends PanacheEntity {

    public String language;
    @Enumerated(EnumType.STRING)
    public ChronoUnit periodUnit;
    public int periodLength;

    @OneToOne
    @JoinColumn(name = "course_id")
    @JsonBackReference(value = "course-settings")
    public Course course;

    public String getFormattedDuration() {
        return periodLength + " " + periodUnit.toString().toLowerCase();
    }

    public CourseSettings() {

    }
}
