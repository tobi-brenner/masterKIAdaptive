package de.htwg.learningpath.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.htwg.course.model.Task;
import de.htwg.course.model.Topic;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
public class LearningStep extends PanacheEntity {

    @Column(length = 16384)
    public String readingMaterial;

    @Column(length = 2048)
    public String explanationText;


    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "learningstep_tasks",
            joinColumns = @JoinColumn(name = "learningstep_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id")
    )
    @JsonManagedReference
    public List<Task> tasks = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "learning_path_id")
    @JsonBackReference
    public LearningPath learningPath;


    @OneToOne(mappedBy = "previousStep")
    @JoinColumn(name = "next_step_id")
    @JsonManagedReference // Forward reference
    public LearningStep nextStep;

    @OneToOne
    @JsonBackReference
    @JoinColumn(name = "previous_step_id")
    public LearningStep previousStep;

    public LearningStep() {
        this.tasks = new ArrayList<>();
    }

    public Long getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return "LearningStep{" +
                "id=" + id +
                ", readingMaterial='" + readingMaterial + '\'' +
                ", explanationText='" + explanationText + '\'' +
                ", tasks=" + tasks +
                ", learningPathId=" + (learningPath != null ? learningPath.id : null) +
                ", previousStepId=" + (previousStep != null ? previousStep.id : null) +
                ", nextStepId=" + (nextStep != null ? nextStep.id : null) +
                '}';
    }

}
