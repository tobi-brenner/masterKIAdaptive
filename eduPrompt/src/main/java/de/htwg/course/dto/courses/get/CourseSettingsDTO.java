package de.htwg.course.dto.courses.get;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.htwg.course.dto.AssessmentDTO;
import de.htwg.course.model.Course;
import de.htwg.course.model.CourseSettings;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToOne;

import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

public class CourseSettingsDTO {
    public Long id;
    public String language;
    public ChronoUnit periodUnit;
    public int periodLength;
    public Course course;


    public CourseSettingsDTO(CourseSettings courseSettings) {
        this.id = courseSettings.id;
        this.language = courseSettings.language;
        this.periodLength = courseSettings.periodLength;
        this.periodUnit = courseSettings.periodUnit;
    }
}
