package de.htwg.learningpath.model;

import de.htwg.course.model.Assessment;
import de.htwg.course.model.Task;
import de.htwg.user.model.User;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class UserTaskAttempt extends PanacheEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User user;

    @ManyToOne
    @JoinColumn(name = "task_id")
    public Task task;

    @ManyToOne
    @JoinColumn(name = "assessment_id", nullable = true)
    public Assessment assessment;

    @ManyToOne
    @JoinColumn(name = "learning_step_id", nullable = true)
    public LearningStep learningStep;


    @ManyToOne
    @JoinColumn(name = "learning_path_id", nullable = true)
    public LearningPath learningPath;

    public LocalDateTime attemptTime;
    public boolean isCorrect;

    @OneToOne(mappedBy = "attempt", cascade = CascadeType.ALL)
    public TaskAnswer answer;

    public Task getTask() {
        return this.task;
    }

    @Override
    public String toString() {
        return "UserTaskAttempt{" +
                "id=" + id +
                ", user=" + user.id +
                ", task=" + task.id +
                ", assessment=" + (assessment != null ? assessment.id : "null") +
                ", learningStep=" + (learningStep != null ? learningStep.id : "null") +
                ", attemptTime=" + attemptTime +
                ", isCorrect=" + isCorrect +
                ", answer=" + (answer != null ? answer.answer : "null") +
                ", feedback=" + (answer != null ? answer.llmResponse : "null") +
                '}';
    }

    public boolean isCorrect() {
        return this.isCorrect;
    }
}
