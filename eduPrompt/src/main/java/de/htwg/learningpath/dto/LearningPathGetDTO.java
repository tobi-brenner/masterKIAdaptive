package de.htwg.learningpath.dto;

import de.htwg.course.dto.courses.get.CourseGetDTO;
import de.htwg.learningpath.model.LearningPath;

import java.util.List;
import java.util.stream.Collectors;

public class LearningPathGetDTO {
    private Long id;
    private CourseGetDTO course;

    private boolean initialAssessmentCompleted;
    private List<LearningStepGetDTO> learningSteps;

    public LearningPathGetDTO(LearningPath learningPath) {
        this.id = learningPath.id;
        this.initialAssessmentCompleted = learningPath.initialAssessmentCompleted;
        this.course = new CourseGetDTO(learningPath.course);
        if (learningPath.learningSteps != null) {
            this.learningSteps = learningPath.learningSteps.stream()
                    .map(LearningStepGetDTO::new)
                    .collect(Collectors.toList());
        }
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CourseGetDTO getCourse() {
        return course;
    }

    public void setCourse(CourseGetDTO course) {
        this.course = course;
    }

    public boolean isInitialAssessmentCompleted() {
        return initialAssessmentCompleted;
    }

    public void setInitialAssessmentCompleted(boolean initialAssessmentCompleted) {
        this.initialAssessmentCompleted = initialAssessmentCompleted;
    }

    public List<LearningStepGetDTO> getLearningSteps() {
        return learningSteps;
    }

    public void setLearningSteps(List<LearningStepGetDTO> learningSteps) {
        this.learningSteps = learningSteps;
    }
}
