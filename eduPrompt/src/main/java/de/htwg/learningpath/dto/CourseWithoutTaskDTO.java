package de.htwg.learningpath.dto;

import de.htwg.course.dto.courses.get.TopicGetDTO;
import de.htwg.course.dto.courses.get.UserGetDTO;
import de.htwg.course.model.Course;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CourseWithoutTaskDTO {
    public Long id;
    public String subject;
    public String description;
    public List<LLMTopicDTO> topics = new ArrayList<>();

    public CourseWithoutTaskDTO(Course course) {
        this.id = course.id;
        this.subject = course.subject;
        this.description = course.description;
        if (course.topics != null) {
            this.topics = course.topics.stream()
                    .map(LLMTopicDTO::new)
                    .collect(Collectors.toList());
        }
    }


    @Override
    public String toString() {
        return "CourseGetDTO{" +
                "id=" + id +
                ", subject='" + subject + '\'' +
                ", description='" + description + '\'' +
                ", topics=" + topics +
                '}';
    }
}
