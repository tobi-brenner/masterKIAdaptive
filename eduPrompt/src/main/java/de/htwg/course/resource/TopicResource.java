package de.htwg.course.resource;

import de.htwg.course.dto.courses.create.TopicCreationDTO;
import de.htwg.course.dto.courses.get.TopicGetDTO;
import de.htwg.course.dto.courses.put.TopicUpdateDTO;
import de.htwg.course.model.Course;
import de.htwg.course.model.Topic;
import de.htwg.learningpath.model.LearningStatistics;
import io.quarkus.logging.Log;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

@Path("/topics")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TopicResource {

    @GET
    @Path("/{id}")
    public Response getTopic(@PathParam("id") Long id) {
        Topic topic = Topic.findById(id);
        if (topic == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Topic not found").build();
        }
        return Response.ok(new TopicGetDTO(topic)).build();
    }

    @GET
    public Response getAllTopics() {
        List<Topic> topics = Topic.listAll();
        List<TopicGetDTO> topicDTOs = topics.stream().map(TopicGetDTO::new).collect(Collectors.toList());
        return Response.ok(topicDTOs).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response createTopic(TopicCreationDTO dto) {
        Log.info("Creating Topic:::");
        Log.info(dto);

        if (dto.courseId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Course ID is required").build();
        }

        Course course = Course.findById(dto.courseId);
        if (course == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Course not found for id: " + dto.courseId).build();
        }

        Topic topic = new Topic();
        topic.name = dto.name;
        topic.description = dto.description;
        topic.orderNumber = dto.orderNumber;
        topic.maxBloom = dto.maxBloom;
        topic.course = course;

        topic.persist();
        return Response.status(Response.Status.CREATED).entity(new TopicGetDTO(topic)).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response updateTopic(@PathParam("id") Long id, TopicUpdateDTO dto) {
        Log.info("Updating Topic:::" + id);
        Topic topic = Topic.findById(id);
        Log.info(dto);
        Log.info(topic);
        if (topic == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Topic not found for id: " + id).build();
        }

        topic.name = dto.name;
        topic.description = dto.description;
        topic.orderNumber = dto.orderNumber;
        topic.maxBloom = dto.maxBloom;

        topic.persist();
        return Response.ok(new TopicGetDTO(topic)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteTopic(@PathParam("id") Long id) {
        Log.info("TOPIC ID:" + id);
        Topic topic = Topic.findById(id);
        Log.info("TOPIC ID:" + topic);
        if (topic == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Topic not found").build();
        }
        List<LearningStatistics> stats = LearningStatistics.find("topic", topic).list();
        if (!stats.isEmpty()) {
            for (LearningStatistics stat : stats) {
                stat.delete();
                stat.persist();
            }
        }
        topic.delete();
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
