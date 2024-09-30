package de.htwg.user.dto;

import de.htwg.course.dto.courses.get.CourseGetDTO;
import de.htwg.learningpath.dto.LearningPathGetDTO;
import de.htwg.learningpath.dto.UserTaskAttemptDTO;
import de.htwg.user.model.User;

import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

public class UserDTO {
    public Long id;
    public String firstName;
    public String lastName;
    public String studentNumber;
    public String email;
    public String preferredLanguage;
    public Set<LearningPathGetDTO> learningPaths;
    public Set<CourseGetDTO> courses;
    public Set<CourseGetDTO> taughtCourses;
    public List<UserTaskAttemptDTO> attempts;

    // Constructors, getters and setters
    public UserDTO() {
    }

    public UserDTO(Long id, String firstName, String lastName, String studentNumber, String email, Set<LearningPathGetDTO> learningPaths, Set<CourseGetDTO> courses, Set<CourseGetDTO> taughtCourses, List<UserTaskAttemptDTO> attempts, String preferredLanguage) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.studentNumber = studentNumber;
        this.email = email;
        this.learningPaths = learningPaths;
        this.courses = courses;
        this.taughtCourses = taughtCourses;
        this.attempts = attempts;
        this.preferredLanguage = preferredLanguage;
    }

    public UserDTO(User user) {
        this.id = user.id;
        this.firstName = user.firstName;
        this.lastName = user.lastName;
        this.studentNumber = user.studentNumber;
        this.email = user.email;
        this.preferredLanguage = user.preferredLanguage;
        if (user.learningPaths != null) {
            this.learningPaths = user.learningPaths.stream()
                    .map(LearningPathGetDTO::new)
                    .collect(Collectors.toSet());
        }
        this.courses = user.courses.stream()
                .map(CourseGetDTO::new)
                .collect(Collectors.toSet());
        this.taughtCourses = user.taughtCourses.stream()
                .map(CourseGetDTO::new)
                .collect(Collectors.toSet());
        this.attempts = user.attempts.stream()
                .map(UserTaskAttemptDTO::new)
                .collect(Collectors.toList());
    }

//    public UserDTO(Long id, String firstName, String lastName, String studentNumber, String email, Set<LearningPath> learningPaths, Set<Course> courses, Set<Course> taughtCourses, List<UserTaskAttempt> attempts) {
//        this.id = id;
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.studentNumber = studentNumber;
//        this.email = email;
//        this.learningPaths = learningPaths;
//        this.courses = courses;
//        this.taughtCourses = taughtCourses;
//        this.attempts = attempts;
//    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", studentNumber='" + studentNumber + '\'' +
                ", email='" + email + '\'' +
                ", learningPaths=" + learningPaths +
                ", courses=" + courses +
                ", taughtCourses=" + taughtCourses +
                ", attempts=" + attempts +
                ", preferredLanguage=" + preferredLanguage +
                '}';
    }
}
