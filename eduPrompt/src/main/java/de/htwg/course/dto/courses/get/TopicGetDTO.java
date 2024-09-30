package de.htwg.course.dto.courses.get;

import de.htwg.course.dto.TaskDTO;
import de.htwg.course.model.BloomLevel;
import de.htwg.course.model.LearningGoal;
import de.htwg.course.model.Topic;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TopicGetDTO {
    public Long id;
    public String name;
    public String description;
    public List<LearningGoalGetDTO> learningGoals;
    public List<TaskDTO> tasks;
    public BloomLevel maxBloom;

    public int orderNumber;

    public TopicGetDTO(String name, String description, List<LearningGoal> learningGoals) {
        this.name = name;
        this.description = description;
        this.learningGoals = learningGoals.stream()
                .map(learningGoal -> new LearningGoalGetDTO(learningGoal))
                .collect(Collectors.toList());
    }

    public TopicGetDTO(Topic topic) {
        this.id = topic.id;
        this.name = topic.name;
        this.description = topic.description;
        this.maxBloom = topic.maxBloom;
        this.orderNumber = topic.orderNumber;
        this.learningGoals = topic.learningGoals != null
                ? topic.learningGoals.stream()
                .map(learningGoal -> new LearningGoalGetDTO(learningGoal))
                .collect(Collectors.toList())
                : new ArrayList<>();

        // If tasks is null, set it to an empty list
        this.tasks = topic.tasks != null
                ? topic.tasks.stream()
                .map(task -> new TaskDTO(task))
                .collect(Collectors.toList())
                : new ArrayList<>();
    }
}