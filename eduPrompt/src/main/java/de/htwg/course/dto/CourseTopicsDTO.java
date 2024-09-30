package de.htwg.course.dto;

import java.util.List;

public class CourseTopicsDTO {
    public Long courseId;
    public List<TopicDTO> topics;

    public CourseTopicsDTO() {
    }

    public CourseTopicsDTO(Long courseId, List<TopicDTO> topics) {
        this.courseId = courseId;
        this.topics = topics;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public List<TopicDTO> getTopics() {
        return topics;
    }

    public void setTopics(List<TopicDTO> topics) {
        this.topics = topics;
    }
}

