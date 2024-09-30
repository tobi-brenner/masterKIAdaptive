package de.htwg.learningpath.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class TaskAnswer extends PanacheEntity {

    @OneToOne
    @JoinColumn(name = "attempt_id")
    public UserTaskAttempt attempt;

    @Column(length = 4096)
    public String answer;

    @Column(length = 4096) // Adjust the length as needed
    public String llmResponse;
}