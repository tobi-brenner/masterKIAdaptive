package de.htwg.course.dto.courses.get;

import de.htwg.ai.resource.LearningGoalsResource;
import de.htwg.course.model.BloomLevel;
import de.htwg.course.model.LearningGoal;

public class LearningGoalGetDTO {
    public Long id;
    public String goal;
    public String description;
    public BloomLevel maxBloom;

    public LearningGoalGetDTO(Long id, String goal, String description) {
        this.id = id;
        this.goal = goal;
        this.description = description;
    }

    public LearningGoalGetDTO(LearningGoal learningGoal) {
        this.id = learningGoal.id;
        this.goal = learningGoal.goal;
        this.description = learningGoal.description;
        this.maxBloom = learningGoal.maxBloom;
    }
}