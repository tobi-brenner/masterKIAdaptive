package de.htwg.course.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class TaskEvaluationResult extends PanacheEntity {
    public boolean isCorrect;
    public String correctAnswer;
    public String hint;

    public TaskEvaluationResult() {
    }

    public TaskEvaluationResult(boolean isCorrect, String answer, String hint) {
    }
}
