package de.htwg.ai.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.htwg.ai.dto.learningGoalGeneration.GenerateLearningGoalDTO;
import de.htwg.ai.dto.learningGoalGeneration.GenerateTopicDTO;
import de.htwg.ai.dto.learningGoalGeneration.TopicsWrapperDTO;
import de.htwg.ai.services.LearningGoalsService;
import de.htwg.course.model.BloomLevel;
import de.htwg.course.model.Course;
import de.htwg.course.model.LearningGoal;
import de.htwg.course.model.Topic;
import de.htwg.studentlevel.StudentLevelResource;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;

@Path("/learninggoal")
public class LearningGoalsResource {

    private static final Logger LOG = Logger.getLogger(StudentLevelResource.class);

    @Inject
    LearningGoalsService learningGoalsService;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    EntityManager entityManager;


    public record LearningGoalDTO(Long courseId, String keyword) {
    }

    /*
     *
     */

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response determineLearningGoals(LearningGoalDTO dto) {
        try {
            Course course = Course.findById(dto.courseId);
            if (course == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Course not found").build();
            }

            String fullPrompt = "further".equalsIgnoreCase(dto.keyword)
                    ? "Generate additional topics and learning goals for the course.\nTitle: " + course.subject + "\nDescription: " + course.description
                    : "Generate topics and learning goals for the course.\nTitle: " + course.subject + "\nDescription: " + course.description;
            String language = course.courseSettings.language;

            String result = learningGoalsService.determineTopicsAndGoals(String.valueOf(course.id), fullPrompt, language);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // Ignore unknown properties
            try {
                Log.info("Start deserialization of topics");
                TopicsWrapperDTO wrapper = objectMapper.readValue(result, TopicsWrapperDTO.class);
                List<GenerateTopicDTO> topics = wrapper.topics;
                Log.info(topics);
                saveTopicsAndGoals(course, topics);

                return Response.ok(result).build();
            } catch (Exception e) {
                Log.error("Error mapping result to topics: " + e.getMessage(), e);
                return Response.status(Response.Status.BAD_REQUEST).entity("Error processing request: " + e.getMessage()).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error processing request: " + e.getMessage()).build();
        }
    }


    @Transactional
    public void saveTopicsAndGoals(Course course, List<GenerateTopicDTO> topics) {
        // Loop through each generated topic and save them
        for (GenerateTopicDTO topicDTO : topics) {
            Topic topic = new Topic();
            topic.name = topicDTO.name;
            topic.description = topicDTO.description;
            topic.orderNumber = topicDTO.orderNumber;
            topic.maxBloom = BloomLevel.valueOf(topicDTO.maxBloom);
            topic.course = course;

            Log.info("Persisting new topic: " + topic.name);

            topic.persist();

            // Loop through and persist the learning goals associated with the topic
            for (GenerateLearningGoalDTO learningGoalDTO : topicDTO.learningGoals) {
                Log.info("LG");
                Log.info(learningGoalDTO);
                LearningGoal learningGoal = new LearningGoal();
                learningGoal.goal = learningGoalDTO.goal;
                learningGoal.description = learningGoalDTO.description;
                learningGoal.maxBloom = BloomLevel.valueOf(learningGoalDTO.maxBloom);
                learningGoal.topic = topic;

                Log.info("Persisting new learning goal: " + learningGoal.goal);

                // Persist the learning goal
                learningGoal.persist();
            }
        }

        // No need to persist the course explicitly; the new topics will be associated with it
        Log.info("All topics and learning goals saved.");
    }

}
