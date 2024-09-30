package de.htwg.learningpath.resource;

import de.htwg.learningpath.dto.FeedbackDTO;
import de.htwg.learningpath.model.Feedback;
import de.htwg.learningpath.model.LearningPath;
import de.htwg.user.model.User;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Path("/feedback")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FeedbackResource {
    @POST
    @Transactional
    @Path("/create")
    public Response createFeedback(FeedbackDTO feedbackDTO) {
        Feedback feedback = new Feedback();
        feedback.learningPath = LearningPath.findById(feedbackDTO.learningPathId);
        feedback.professor = User.findById(feedbackDTO.professorId);
        feedback.content = feedbackDTO.content;
        feedback.createdAt = new Date();
        feedback.feedbackType = feedbackDTO.feedbackType;
        feedback.isIngestedToLLM = feedbackDTO.isIngestedToLLM;

        feedback.persist();

        FeedbackDTO createdFeedbackDTO = new FeedbackDTO(feedback);

        return Response.status(Response.Status.CREATED).entity(createdFeedbackDTO).build();
    }



    @GET
    @Path("/list/{learningPathId}")
    public Response listFeedbacks(@PathParam("learningPathId") Long learningPathId) {
        List<Feedback> feedbacks = Feedback.list("learningPath.id", learningPathId);
        List<FeedbackDTO> feedbackDTOs = feedbacks.stream()
                .map(FeedbackDTO::new)  // Convert each Feedback to FeedbackDTO
                .collect(Collectors.toList());

        return Response.ok(feedbackDTOs).build();
    }



}
