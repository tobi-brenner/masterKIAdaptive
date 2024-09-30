package de.htwg.course.resource;

import de.htwg.course.dto.AssessmentDTO;
import de.htwg.course.model.Assessment;
import de.htwg.course.model.Task;
import de.htwg.course.services.AssessmentService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/assessment")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AssessmentResource {

    @Inject
    AssessmentService assessmentService;

    @GET
    public List<Assessment> getAssessments() {
        return Assessment.listAll();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAssessment(@PathParam("id") Long id) {
        Assessment assessment = Assessment.findById(id);
        if (assessment != null) {
            return Response.ok(new AssessmentDTO(assessment)).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Assessment not found for ID: " + id).build();
        }
    }

    @POST
    @Path("/add-task/{taskId}/{assessmentId}")
    @Transactional
    public Response addTaskToAssessment(@PathParam("taskId") Long taskId, @PathParam("assessmentId") Long assessmentId) {
        Task task = Task.findById(taskId);
        Assessment assessment = Assessment.findById(assessmentId);
        if (task != null && assessment != null) {
            task.assessment = assessment;
            assessment.tasks.add(task);
            task.persist();
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("Task or Assessment not found").build();
    }

    @POST
    @Path("/remove-task/{taskId}")
    @Transactional
    public Response removeTaskFromAssessment(@PathParam("taskId") Long taskId) {
        Task task = Task.findById(taskId);
        if (task != null && task.assessment != null) {
            Assessment assessment = task.assessment;
            task.assessment = null;
            assessment.tasks.remove(task);
            task.persist();
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("Task or Assessment not found").build();
    }

}
