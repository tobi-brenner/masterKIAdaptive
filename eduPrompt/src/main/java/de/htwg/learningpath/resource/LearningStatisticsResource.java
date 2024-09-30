package de.htwg.learningpath.resource;

import de.htwg.learningpath.dto.LearningStatisticsGetDTO;
import de.htwg.learningpath.model.LearningStatistics;
import de.htwg.course.model.Course;
import de.htwg.course.model.Topic;
import de.htwg.user.model.User;
import io.quarkus.logging.Log;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

@Path("/learning-statistics")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LearningStatisticsResource {

    @GET
    @Path("/{userId}/learning-path/{learningPathId}/course/{courseId}")
    public Response getLearningStatistics(@PathParam("userId") Long userId,
                                          @PathParam("learningPathId") Long learningPathId,
                                          @PathParam("courseId") Long courseId) {

        Log.info(userId);
        Log.info(learningPathId);
        Log.info(courseId);

        List<LearningStatistics> statistics = LearningStatistics.find("user.id = ?1 and learningPath.id = ?2 and topic.course.id = ?3",
                userId, learningPathId, courseId).list();
        Log.info(statistics);
        if (statistics.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("No learning statistics found for the given criteria.").build();
        }

        List<LearningStatisticsGetDTO> statisticsDTOs = statistics.stream()
                .map(LearningStatisticsGetDTO::new)
                .collect(Collectors.toList());

        Log.info(statisticsDTOs);
        return Response.ok(statisticsDTOs).build();
    }


}


