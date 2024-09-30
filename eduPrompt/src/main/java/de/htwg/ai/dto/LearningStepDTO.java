package de.htwg.ai.dto;

import de.htwg.course.dto.initassessment.TaskDTO;
import de.htwg.course.dto.initassessment.TopicDTO;

import java.util.List;

public class LearningStepDTO {

    public List<Long> existingTasks;

    public String readingMaterial;
    public String explanationText;

    public List<TopicDTO> topics;
    public List<TaskDTO> tasks;
}
