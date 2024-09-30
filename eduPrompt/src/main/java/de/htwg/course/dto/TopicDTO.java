package de.htwg.course.dto;

import java.util.ArrayList;
import java.util.List;

public class TopicDTO {
    public String topic;
    public List<String> learningGoals = new ArrayList<>();

    public TopicDTO() {
    }

    public TopicDTO(String topic, List<String> learningGoals) {
        this.topic = topic;
        if (learningGoals != null) {
            this.learningGoals = learningGoals;
        }
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<String> getLearningGoals() {
        return learningGoals;
    }

    public void setLearningGoals(List<String> learningGoals) {
        this.learningGoals = learningGoals != null ? learningGoals : new ArrayList<>();
    }
}

