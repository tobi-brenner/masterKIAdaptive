package de.htwg.course.dto;

import de.htwg.course.model.Assessment;
import de.htwg.course.model.Task;

import java.util.List;
import java.util.stream.Collectors;

public class AssessmentDTO {
    public Long id;
    public Boolean isInitial;
    public String description;
    public List<TaskDTO> tasks;

    public AssessmentDTO(Assessment assessment) {
        this.id = assessment.id;
        this.isInitial = assessment.isInitial;
        this.description = assessment.description;
        this.tasks = assessment.tasks.stream().map(TaskDTO::new).collect(Collectors.toList());
    }
}

