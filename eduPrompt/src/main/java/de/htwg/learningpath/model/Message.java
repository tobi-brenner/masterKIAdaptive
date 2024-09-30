package de.htwg.learningpath.model;


import de.htwg.course.model.Course;
import de.htwg.user.model.User;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "message")
public class Message extends PanacheEntity {

    @ManyToOne
    @JoinColumn(name = "learning_path_id")
    public LearningPath learningPath;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    public User sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    public User recipient;

    @Column(nullable = false)
    public String content;

    @Column(nullable = false)
    public Date createdAt;
}
