package de.htwg.course.dto.mapper;

import de.htwg.course.dto.courses.get.LearningGoalGetDTO;
import de.htwg.course.model.LearningGoal;

public class LearningGoalMapper {
    public static LearningGoalGetDTO toDTO(LearningGoal learningGoal) {
        return new LearningGoalGetDTO(
                learningGoal.id,
                learningGoal.goal,
                learningGoal.description
        );
    }

    public static LearningGoal toEntity(LearningGoalGetDTO dto) {
        LearningGoal learningGoal = new LearningGoal();
        learningGoal.id = dto.id;
        learningGoal.goal = dto.goal;
        learningGoal.description = dto.description;
        return learningGoal;
    }
}

