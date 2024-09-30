package de.htwg.ai.dto.learningGoalGeneration;

import java.util.List;

public class GenerateTopicDTO {
    public String name;
    public String description;
    public int orderNumber;
    public String maxBloom;
    public List<GenerateLearningGoalDTO> learningGoals;
}
