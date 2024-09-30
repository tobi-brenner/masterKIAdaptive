package de.htwg.course.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.htwg.learningpath.model.LearningStep;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.inject.Inject;
import jakarta.persistence.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "task_type")
public abstract class Task extends PanacheEntity {

    @Inject
    ObjectMapper objectMapper;

    @Column(length = 1024)
    public String question;
    public BloomLevel bloomLevel;
    @Column(length = 1024)
    public String correctAnswer = "";

    public boolean verified = false;



    @ManyToMany(mappedBy = "tasks")
    @JsonIgnore
    public List<LearningStep> learningSteps;

    //TODO: llmEvaluation: boolean -> if true llm evaluates solution, if false take solution given by prof
//    @Column(length = 1024)
//    public String options;

    @ManyToOne
    @JoinColumn(name = "topic_id")
//    @JsonIgnore
    @JsonBackReference(value = "topic-tasks")
    public Topic topic;

    @ManyToOne
    @JoinColumn(name = "assessment_id")
    @JsonBackReference(value = "assessment-tasks")
    public Assessment assessment;

    @Column(name = "task_type", insertable = false, updatable = false)
    public String taskType;

    public String hint;

    // Ensure the correctAnswer is stored as a JSON string
    public void setCorrectAnswer(Map<String, String> correctAnswer) {
        try {
            this.correctAnswer = objectMapper.writeValueAsString(correctAnswer);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize correctAnswer to JSON", e);
        }
    }

    // Method to retrieve correctAnswer as a Map
    public Map<String, String> getCorrectAnswer() {
        try {
            return objectMapper.readValue(this.correctAnswer, new TypeReference<Map<String, String>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize correctAnswer from JSON", e);
        }
    }


    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", bloomLevel=" + bloomLevel +
                ", correctAnswer='" + correctAnswer + '\'' +
                ", verified=" + verified +
                ", learningSteps=" + (learningSteps != null ? learningSteps.stream().map(LearningStep::getId).collect(Collectors.toList()) : null) +
                ", topicId=" + (topic != null ? topic.id : null) +
                ", assessmentId=" + (assessment != null ? assessment.id : null) +
                ", taskType='" + taskType + '\'' +
                ", hint='" + hint + '\'' +
                '}';
    }

}
