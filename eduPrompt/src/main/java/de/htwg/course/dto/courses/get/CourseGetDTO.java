package de.htwg.course.dto.courses.get;

import de.htwg.course.dto.AssessmentDTO;
import de.htwg.course.model.Course;
import io.quarkus.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CourseGetDTO {
    public Long id;
    public String subject;
    public String description;
    public UserGetDTO prof;
    public List<TopicGetDTO> topics = new ArrayList<>();
    public CourseMaterialGetDTO courseMaterial;
    public AssessmentDTO assessment;
    public CourseSettingsDTO courseSettings;

    public CourseGetDTO(Long id, String subject, String description, List<TopicGetDTO> topics, UserGetDTO prof) {
        this.id = id;
        this.subject = subject;
        this.description = description;
        this.prof = prof;
        if (topics != null) {
            this.topics = topics;
        }
    }


    public CourseGetDTO(Course course) {
        this.id = course.id;
        this.subject = course.subject;
        this.description = course.description;
        this.prof = new UserGetDTO(course.prof);
        if (course.courseSettings != null) {
            this.courseSettings = new CourseSettingsDTO(course.courseSettings);
        }
        if (course.assessment != null) {
            this.assessment = new AssessmentDTO(course.assessment);
        }
        if (course.topics != null) {
            this.topics = course.topics.stream()
                    .map(TopicGetDTO::new)
                    .collect(Collectors.toList());
        }
        this.courseMaterial = course.courseMaterial != null ? new CourseMaterialGetDTO(course.courseMaterial) : null;
    }


    @Override
    public String toString() {
        return "CourseGetDTO{" +
                "id=" + id +
                ", subject='" + subject + '\'' +
                ", description='" + description + '\'' +
                ", prof=" + prof +
                ", topics=" + topics +
                ", courseMaterial=" + courseMaterial +
                ", courseSettings=" + courseSettings +
                '}';
    }
}