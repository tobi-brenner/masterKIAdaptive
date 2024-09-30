package de.htwg.course.dto.mapper;


import de.htwg.course.dto.courses.get.TopicGetDTO;
import de.htwg.course.model.Topic;

import java.util.stream.Collectors;

public class TopicMapper {
    public static TopicGetDTO toDTO(Topic topic) {
        return new TopicGetDTO(topic);

    }

    public static Topic toEntity(TopicGetDTO dto) {
        Topic topic = new Topic();
        topic.id = dto.id;
        topic.name = dto.name;
        topic.description = dto.description;
        topic.learningGoals = dto.learningGoals != null ? dto.learningGoals.stream().map(LearningGoalMapper::toEntity).collect(Collectors.toList()) : null;
        return topic;
    }
}
