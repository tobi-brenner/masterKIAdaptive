package de.htwg.course.dto;


import de.htwg.course.dto.courses.get.TopicGetDTO;
import de.htwg.course.model.LearningGoal;
import de.htwg.course.model.Topic;

import java.time.temporal.ChronoUnit;
import java.util.List;

public class CourseUpdateDTO {
    public String subject;
    public String description;
    public List<TopicGetDTO> topics;
    public List<Long> deletedTopics;
    public List<Long> deletedLearningGoals;
    public String language;
    public ChronoUnit periodUnit;
    public int periodLength;


}
