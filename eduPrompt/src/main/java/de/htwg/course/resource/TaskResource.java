package de.htwg.course.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.htwg.ai.services.course.TaskGenerationService;
import de.htwg.course.dto.GeneratedTasksResponseDTO;
import de.htwg.course.dto.TaskCreateDTO;
import de.htwg.course.factory.task.TaskFactory;
import de.htwg.course.factory.task.TaskFactoryProducer;
import de.htwg.course.model.Course;
import de.htwg.course.model.Task;
import de.htwg.course.dto.TaskDTO;
import de.htwg.course.model.Topic;
import de.htwg.course.services.TaskService;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/tasks")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TaskResource {

    @Inject
    TaskService taskService;

    @Inject
    TaskGenerationService taskGenerationService;


    @POST
    @Transactional
    public Response createTask(TaskCreateDTO taskDTO) {
        Task task = taskService.createTask(taskDTO);
        return Response.status(Response.Status.CREATED).entity(task).build();
    }

    public record GenerateTasksDTO(Long courseId, Long topicId, String keyword) {
    }


    @POST
    @Path("/generate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response generateCourseTasks(GenerateTasksDTO dto) {
        Log.info("GEN TASKS " + dto);
        Log.info(dto.courseId);
        Log.info(dto.topicId);
        Log.info(dto.keyword);
        try {
            Course course = Course.findById(dto.courseId);
            if (course == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Course not found").build();
            }
            String topicPrompt = null;
            Topic topic = null;
            for (Topic t : course.topics) {
                if (t.id.equals(dto.topicId)) {
                    topicPrompt = t.toString();
                    topic = t;
                    break;
                }
            }

            if (topicPrompt == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Topic not found").build();
            }

            Log.info("GEEEEEEEEEEEEEEEN::");
            String result = taskGenerationService.generate(String.valueOf(course.id) + "-" + String.valueOf(dto.topicId), course.toString(), topicPrompt, dto.keyword, course.courseSettings.language);
            Log.info("TASK GEN RESULT::" + result);
            // Parse the JSON result into the GeneratedTasksResponseDTO
            GeneratedTasksResponseDTO generatedTasks = taskService.parseGeneratedTasks(result);
            Log.info("TASK GEN RESULT PARSED::" + generatedTasks);
            List<GeneratedTasksResponseDTO.TaskDTO> responseTasks = new ArrayList<>();

            // Iterate over the parsed tasks and use the factory to create and persist them
            for (GeneratedTasksResponseDTO.TaskDTO taskDTO : generatedTasks.tasks) {
                TaskFactory factory = TaskFactoryProducer.getFactory(taskDTO);
                Task task = factory.createTask(taskDTO);

                task.topic = topic;
                task.persist();
            }
            List<Task> persistedTasks = Task.find("topic", topic).list();
            List<TaskDTO> taskDTOs = persistedTasks.stream()
                    .map(TaskDTO::new)
                    .collect(Collectors.toList());

            return Response.ok(taskDTOs).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error processing request: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateTask(@PathParam("id") Long id, TaskDTO taskDTO) {
        Task task = taskService.updateTask(id, taskDTO);
        if (task == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(task).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteTask(@PathParam("id") Long id) {
        boolean deleted = taskService.deleteTask(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    @GET
    @Path("/{id}")
    public Response getTask(@PathParam("id") Long id) {
        Task task = taskService.getTask(id);
        if (task == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(task).build();
    }

    @GET
    public Response getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return Response.ok(tasks).build();
    }
}
