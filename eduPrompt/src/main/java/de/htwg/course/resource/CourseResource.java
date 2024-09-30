package de.htwg.course.resource;

import de.htwg.course.dto.AssessmentDTO;
import de.htwg.course.dto.CourseTopicsDTO;
import de.htwg.course.dto.CourseUpdateDTO;
import de.htwg.course.dto.TopicDTO;
import de.htwg.course.dto.courses.get.LearningGoalGetDTO;
import de.htwg.course.dto.courses.get.TopicGetDTO;
import de.htwg.course.dto.courses.get.CourseGetDTO;
import de.htwg.course.dto.courses.get.UserGetDTO;
import de.htwg.course.dto.mapper.CourseMapper;
import de.htwg.course.model.*;
import de.htwg.course.services.CourseService;
import de.htwg.user.dto.CourseCreationDTO;
import de.htwg.user.model.User;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.*;
import java.util.stream.Collectors;


@Path("/courses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CourseResource {

    @Inject
    CourseService courseService;

    @Inject
    EntityManager entityManager;


    @GET
    public List<CourseGetDTO> getCourses() {
        List<Course> courses = Course.listAll();
        return courses.stream()
                .map(CourseGetDTO::new)  // Use the constructor that takes a Course object
                .collect(Collectors.toList());
    }




    @GET
    @Path("/{courseId}/assessment")
    public Response getAssessmentByCourseId(@PathParam("courseId") Long courseId) {
        Assessment assessment = Assessment.find("course.id", courseId).firstResult();
        if (assessment == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        AssessmentDTO assessmentDTO = new AssessmentDTO(assessment);
        return Response.ok(assessmentDTO).build();
    }

    @GET
    @Path("/{id}")
    public Response getCourse(@PathParam("id") Long id) {


        Course course = Course.findById(id);

        // If the course is not found, return a 404 Not Found response
        if (course == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Course not found for id: " + id)
                    .build();
        }

        // If the course is found, map it to the DTO and return it
        CourseGetDTO courseDTO = new CourseGetDTO(course);
        return Response.ok(courseDTO).build();
    }


    @POST
    @Transactional
    public Response createCourse(CourseCreationDTO dto) {
        try {
            Log.info("CREATING...");
            Log.info(dto);
            // Create a new course instance
            Course course = new Course();
            course.subject = dto.subject;
            course.description = dto.description;

            // Set up CourseMaterial
            CourseMaterial courseMaterial = new CourseMaterial();
            courseMaterial.course = course;
            course.courseMaterial = courseMaterial;

            // Assign professor if provided
            if (dto.profUserId != null) {
                User prof = User.findById(dto.profUserId);
                if (prof == null) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("Professor with ID " + dto.profUserId + " not found")
                            .build();
                }
                course.prof = prof;
            }

            // Create and configure CourseSettings
            CourseSettings courseSettings = new CourseSettings();
            courseSettings.language = dto.language;
            courseSettings.periodUnit = dto.periodUnit;
            courseSettings.periodLength = dto.periodLength;
            courseSettings.course = course;

            Log.info(course);
            Log.info(courseSettings);

            // Assign CourseSettings to the course
            course.courseSettings = courseSettings;

            // Persist the course (this will cascade persist CourseSettings and CourseMaterial)
            Course.persist(course);
            Log.info(course);

            // If course persistence was successful, return the created course details
            CourseGetDTO courseGetDTO = new CourseGetDTO(course);
            Log.info(courseGetDTO);
            return Response.status(Response.Status.CREATED).entity(courseGetDTO).build();

        } catch (Exception e) {
            // Log the exception details for debugging
            Log.error("Error creating course", e);
            Log.info("EROOR", e);

            // Return a generic error response
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An error occurred while creating the course. Please try again later.")
                    .build();
        }
    }


    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response updateCourse(@PathParam("id") Long id, CourseUpdateDTO courseUpdateDTO) {
        Log.info("Update Course...");
        Course course = Course.findById(id);
        if (course == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Course not found").build();
        }
        try {
            course.subject = courseUpdateDTO.subject;
            course.description = courseUpdateDTO.description;
            course.courseSettings.periodUnit = courseUpdateDTO.periodUnit;
            course.courseSettings.periodLength = courseUpdateDTO.periodLength;
            course.courseSettings.language = courseUpdateDTO.language;


            updateTopics(course, courseUpdateDTO.topics);

            // Delete topics
            if (courseUpdateDTO.deletedTopics != null) {
                for (Long topicId : courseUpdateDTO.deletedTopics) {
                    Topic topic = Topic.findById(topicId);
                    if (topic != null && topic.course.equals(course)) {
                        topic.delete();
                    }
                }
            }

            // Delete learning goals
            if (courseUpdateDTO.deletedLearningGoals != null) {
                for (Long goalId : courseUpdateDTO.deletedLearningGoals) {
                    LearningGoal goal = LearningGoal.findById(goalId);
                    if (goal != null) {
                        goal.delete();
                    }
                }
            }

            course.persist();
            // Eagerly fetch files
            if (course.courseMaterial != null) {
                course.courseMaterial.files.size();
            }
            Log.info(CourseMapper.toDTO(course));
            return Response.ok(CourseMapper.toDTO(course)).build();

        } catch (Exception e) {
            Log.error(e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error updating course: " + e.getMessage()).build();
        }

    }

    private void updateTopics(Course course, List<TopicGetDTO> topicDTOs) {
        Map<Long, Topic> existingTopicsMap = course.topics.stream()
                .collect(Collectors.toMap(t -> t.id, t -> t));

        for (TopicGetDTO topicDTO : topicDTOs) {
            Topic topic;
            if (topicDTO.id != null && existingTopicsMap.containsKey(topicDTO.id)) {
                // Update existing topic
                topic = existingTopicsMap.remove(topicDTO.id);
                topic.name = topicDTO.name;
                topic.description = topicDTO.description;
            } else {
                // Add new topic
                topic = new Topic();
                topic.name = topicDTO.name;
                topic.description = topicDTO.description;
                topic.course = course;
                if (course.topics == null) {
                    course.topics = new ArrayList<>();
                }
                course.topics.add(topic);
                entityManager.persist(topic); // Ensure the topic is saved before saving the learning goals
            }
            updateLearningGoals(topic, topicDTO.learningGoals);
            entityManager.merge(topic);
        }

        // Remove topics that are no longer present
        for (Topic remainingTopic : existingTopicsMap.values()) {
            if (remainingTopic.learningGoals != null) {
                for (LearningGoal goal : remainingTopic.learningGoals) {
                    entityManager.remove(goal);
                }
            }
            entityManager.remove(remainingTopic);
        }
    }

    private void updateLearningGoals(Topic topic, List<LearningGoalGetDTO> learningGoalDTOs) {
        if (topic.learningGoals == null) {
            topic.learningGoals = new ArrayList<>();
        }

        Map<Long, LearningGoal> existingGoalsMap = topic.learningGoals.stream()
                .collect(Collectors.toMap(g -> g.id, g -> g));

        for (LearningGoalGetDTO goalDTO : learningGoalDTOs) {
            LearningGoal goal;
            if (goalDTO.id != null && existingGoalsMap.containsKey(goalDTO.id)) {
                // Update existing goal
                goal = existingGoalsMap.remove(goalDTO.id);
                goal.goal = goalDTO.goal;
                goal.description = goalDTO.description;
            } else {
                // Add new goal
                goal = new LearningGoal();
                goal.goal = goalDTO.goal;
                goal.description = goalDTO.description;
                goal.topic = topic;
                if (topic.learningGoals == null) {
                    topic.learningGoals = new ArrayList<>();
                }
                topic.learningGoals.add(goal);
            }
            entityManager.persist(goal);
        }

        // Remove goals that are no longer present
        for (LearningGoal remainingGoal : existingGoalsMap.values()) {
            entityManager.remove(remainingGoal);
        }
    }


    @PUT
    @Path("/{id}/topics")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCourseTopics(@PathParam("id") Long courseId, CourseTopicsDTO courseTopicsDTO) {
        if (!courseId.equals(courseTopicsDTO.getCourseId())) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Mismatched course ID").build();
        }

        Course course = Course.findById(courseId);
        if (course == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        course.topics.clear(); // Clear existing topics
        for (TopicDTO topicDTO : courseTopicsDTO.getTopics()) {
            Topic topic = new Topic();
            topic.name = topicDTO.getTopic();
            topic.course = course;
            topic.learningGoals = topicDTO.getLearningGoals().stream().map(lg -> {
                LearningGoal learningGoal = new LearningGoal();
                learningGoal.description = lg;
                learningGoal.topic = topic;
                return learningGoal;
            }).collect(Collectors.toList());

            course.topics.add(topic); // Add the new topic to the course
        }

        course.persist();
        Log.info("----------------------");
        Log.info(course);
        Log.info("----------------------");
        return Response.ok(course).build(); // Return the updated course
    }

    private void logLearningGoal(String lg, LearningGoal learningGoal) {
        System.out.println("lg: " + lg);
        System.out.println("LearningGoal: " + learningGoal.description);
    }



    @DELETE
    @Path("/{id}")
    public Response deleteCourse(@PathParam("id") Long id) {
        Course entity = Course.findById(id);
        if (entity == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        entity.delete();
        return Response.noContent().build();
    }



}

