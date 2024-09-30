package de.htwg.user.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.htwg.course.model.Course;
import de.htwg.learningpath.model.Feedback;
import de.htwg.learningpath.model.LearningPath;
import de.htwg.learningpath.model.Message;
import de.htwg.learningpath.model.UserTaskAttempt;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.logging.Log;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
public class User extends PanacheEntity {

    public String firstName;
    public String lastName;
    public String studentNumber;
    public String email;
    public String password;
    public String preferredLanguage;
    public String role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonManagedReference
    @JsonIgnore
    public Set<LearningPath> learningPaths = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "user_courses",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    @JsonManagedReference(value = "course-users")
    public Set<Course> courses = new HashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "prof", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<Course> taughtCourses = new HashSet<>();

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    public List<UserTaskAttempt> attempts;

    @JsonIgnore
    @OneToMany(mappedBy = "professor")
    public List<Feedback> feedbacks;

    @JsonIgnore
    @OneToMany(mappedBy = "sender")
    public List<Message> sentMessages;

    @JsonIgnore
    @OneToMany(mappedBy = "recipient")
    public List<Message> receivedMessages;

    @Override
    public String toString() {
        return "User{" +
                ", id=" + id +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", studentNumber='" + studentNumber + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", preferredLangauge='" + preferredLanguage + '\'' +
                ", role='" + role + '\'' +
//                ", learningPaths=" + learningPaths +
//                ", courses=" + courses +
//                ", taughtCourses=" + taughtCourses +
                '}';
    }

    /**
     * Specific additional queries
     */

    public static User findByEmail(String email) {
        User foundUser = find("email", email).firstResult();
        Log.info("FOUND USER" + foundUser);
        return find("email", email).firstResult();
    }

}
