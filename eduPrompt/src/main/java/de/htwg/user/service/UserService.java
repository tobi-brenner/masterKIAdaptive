package de.htwg.user.service;

import de.htwg.course.dto.courses.get.CourseGetDTO;
import de.htwg.course.dto.courses.get.TopicGetDTO;
import de.htwg.course.dto.courses.get.UserGetDTO;
import de.htwg.course.model.Course;
import de.htwg.learningpath.dto.LearningPathGetDTO;
import de.htwg.learningpath.dto.UserTaskAttemptDTO;
import de.htwg.learningpath.model.LearningPath;
import de.htwg.user.dto.UserDTO;
import de.htwg.user.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserService {


    private static final Logger LOG = Logger.getLogger(UserService.class.getName());
    @PersistenceContext
    EntityManager em;

    @Transactional
    public User enrollUserToCourse(Long userId, Long courseId) {

        User user = User.findById(userId);
        if (user == null) {
            throw new IllegalStateException("User with ID " + userId + " not found");
        }
        Course course = Course.findById(courseId);
        if (course == null) {
            throw new IllegalStateException("Course with ID " + courseId + " not found");
        }
        user.courses.add(course);
        // init learningpath of user on enrollment
        LearningPath learningPath = new LearningPath();
        learningPath.user = user;
        learningPath.course = course;
        learningPath.initialAssessmentCompleted = false;
        user.learningPaths.add(learningPath);
        user.persist();
        if (course.courseMaterial != null && course.courseMaterial.files != null) {
            course.courseMaterial.files.size(); // Trigger initialization
        }
        return user;
    }

    @Transactional
    public List<CourseGetDTO> findCoursesByUserId(Long id) {
        User user = User.findById(id);
        return user.courses.stream()
                .map(course -> {
                    UserGetDTO prof = new UserGetDTO(course.prof);
                    List<TopicGetDTO> topicGetDTOS = course.topics.stream()
                            .map(topic -> new TopicGetDTO(topic.name, topic.description, topic.learningGoals))  // Adapt to your Topic entity structure
                            .collect(Collectors.toList());
                    return new CourseGetDTO(course.id, course.subject, course.description, topicGetDTOS, prof);
                })
                .collect(Collectors.toList());
//        user.courses.size();
//        Hibernate.initialize(user.courses);
//        Set<Course> courses = user.courses;
//        Log.info("-------------------------");
//        Log.info(user.courses);
//        Log.info(user.firstName);
//        Log.info(courses);
//        Log.info("-------------------------");
//        return user.courses;  // Ensure courses are fetched lazily or handle transaction
    }

    public User findUserWithCourses(Long userId) {
        String qlString = "SELECT u FROM User u LEFT JOIN FETCH u.courses WHERE u.id = :userId";
        return em.createQuery(qlString, User.class)
                .setParameter("userId", userId)
                .getSingleResult();
    }

    @Transactional
    public Set<Course> getUserCourses(Long userId) {
        User user = em.find(User.class, userId);
        if (user != null) {
            // Initialize courses collection
            user.courses.size();  // Access the collection to trigger the loading due to FetchType.LAZY
            Hibernate.initialize(user.courses);
            return user.courses;
        }
        return null;
    }


    public UserDTO convertToDTO(User user) {
        if (user == null) {
            return null;
        }
        Set<LearningPathGetDTO> learningPathDTOs = user.learningPaths.stream()
                .map(lp -> new LearningPathGetDTO(lp)) // Add other fields as needed
                .collect(Collectors.toSet());

        Set<CourseGetDTO> courseDTOs = user.courses.stream()
                .map(c -> new CourseGetDTO(c)) // Add other fields as needed
                .collect(Collectors.toSet());

        Set<CourseGetDTO> taughtCourseDTOs = user.taughtCourses.stream()
                .map(c -> new CourseGetDTO(c)) // Add other fields as needed
                .collect(Collectors.toSet());

        List<UserTaskAttemptDTO> attemptDTOs = user.attempts.stream()
                .map(a -> new UserTaskAttemptDTO(a))
                .collect(Collectors.toList());

        return new UserDTO(
                user.id,
                user.firstName,
                user.lastName,
                user.studentNumber,
                user.email,
                learningPathDTOs,
                courseDTOs,
                taughtCourseDTOs,
                attemptDTOs,
                user.preferredLanguage
        );
    }
}
