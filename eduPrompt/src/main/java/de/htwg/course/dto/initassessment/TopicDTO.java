package de.htwg.course.dto.initassessment;

import de.htwg.course.dto.initassessment.TaskDTO;

import java.util.List;

public class TopicDTO {
    private String topicName;
    private Long topicId;
    private List<TaskDTO> tasks;

    // Getters and setters
    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public List<TaskDTO> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskDTO> tasks) {
        this.tasks = tasks;
    }
}

