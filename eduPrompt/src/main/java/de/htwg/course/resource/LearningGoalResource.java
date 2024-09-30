package de.htwg.course.resource;

import de.htwg.course.dto.courses.get.LearningGoalGetDTO;
import de.htwg.course.model.LearningGoal;
import de.htwg.course.model.Topic;
import io.quarkus.logging.Log;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

@Path("/learninggoals")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LearningGoalResource {


    @GET
    @Path("/{id}")
    public Response getLearningGoal(@PathParam("id") Long id) {
        LearningGoal learningGoal = LearningGoal.findById(id);
        if (learningGoal == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Learning goal not found").build();
        }
        return Response.ok(new LearningGoalGetDTO(learningGoal)).build();
    }

    @GET
    public Response getAllLearningGoals() {
        List<LearningGoal> learningGoals = LearningGoal.listAll();
        List<LearningGoalGetDTO> learningGoalDTOs = learningGoals.stream()
                .map(LearningGoalGetDTO::new)
                .collect(Collectors.toList());
        return Response.ok(learningGoalDTOs).build();
    }

    @POST
    @Transactional
    public Response createLearningGoal(LearningGoal learningGoal) {
        if (learningGoal.id != null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Id was invalidly set on request.").build();
        }

        Topic topic = Topic.findById(learningGoal.topic.id);
        if (topic == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Topic not found for id: " + learningGoal.topic.id).build();
        }
        learningGoal.topic = topic;
        learningGoal.persist();
        return Response.status(Response.Status.CREATED).entity(new LearningGoalGetDTO(learningGoal)).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateLearningGoal(@PathParam("id") Long id, LearningGoal learningGoal) {
        LearningGoal existingGoal = LearningGoal.findById(id);
        if (existingGoal == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Learning goal not found").build();
        }

        existingGoal.goal = learningGoal.goal;
        existingGoal.description = learningGoal.description;
        existingGoal.maxBloom = learningGoal.maxBloom;
        existingGoal.persist();

        return Response.ok(new LearningGoalGetDTO(existingGoal)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteLearningGoal(@PathParam("id") Long id) {
        Log.info("DELETE LearningGoal:::");
        LearningGoal learningGoal = LearningGoal.findById(id);
        Log.info(learningGoal);
        if (learningGoal == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Learning goal not found").build();
        }
        learningGoal.delete();
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
