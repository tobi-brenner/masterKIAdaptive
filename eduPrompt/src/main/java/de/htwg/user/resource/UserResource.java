package de.htwg.user.resource;

import de.htwg.course.dto.courses.get.CourseGetDTO;
import de.htwg.course.dto.courses.get.UserGetDTO;
import de.htwg.course.model.Course;
import de.htwg.course.resource.FileUploadResource;
import de.htwg.learningpath.model.LearningPath;
import de.htwg.user.dto.EnrollmentRequest;
import de.htwg.user.dto.LoggedInUserDTO;
import de.htwg.user.dto.LoginUser;
import de.htwg.user.dto.UserDTO;
import de.htwg.user.model.User;
import de.htwg.user.service.UserService;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    private static final Logger LOG = Logger.getLogger(UserResource.class.getName());

    @Inject
    UserService userService;

    //    @GET
//    public List<User> getUsers() {
//        return User.listAll();
//    }

//    @GET
//    public List<User> getUsers(@QueryParam("query") String query) {
//        Log.info(query);
//        if (query == null || query.isEmpty()) {
//            return User.listAll();
//        } else {
//            return User.find("lower(firstName) like lower(?1) or lower(lastName) like lower(?1)", "%" + query + "%").list();
//        }
//    }

    @GET
    @Transactional
    public Response getUsers(@QueryParam("query") String query) {
        Log.info(query);
        List<User> users;
        if (query == null || query.isEmpty()) {
            users = User.listAll();
        } else {
            users = User.find("lower(firstName) like lower(?1) or lower(lastName) like lower(?1)", "%" + query + "%").list();
        }
        List<UserDTO> userDTOs = users.stream().map(UserDTO::new).collect(Collectors.toList());
        Log.info(userDTOs);
        return Response.ok(userDTOs).build();
    }

    @GET
    @Path("/enrolled")
    @Transactional
    public Response getEnrolledUsers(@QueryParam("courseId") Long courseId) {
        if (courseId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Course ID is required").build();
        }

        // Fetch all users who have a learning path associated with the given course
        List<User> users = User.find("SELECT DISTINCT u FROM User u JOIN u.learningPaths lp WHERE lp.course.id = ?1", courseId).list();

        // Convert users to DTOs if necessary
        List<UserDTO> userDTOs = users.stream().map(UserDTO::new).collect(Collectors.toList());

        return Response.ok(userDTOs).build();
    }

    @GET
    @Path("/{userId}/learningpaths")
    @Produces(MediaType.APPLICATION_JSON)
    public List<LearningPath> getUserLearningPaths(@PathParam("userId") Long userId) {
        Log.info("GET LEARNING PATHS");
        User user = User.findById(userId);
        if (user == null) {
            throw new WebApplicationException("User not found", 404);
        }
        // Initialize lazy-loaded collection
        user.learningPaths.size();
        Log.info(user.learningPaths);
        Log.info(user.learningPaths.size());
        return user.learningPaths.stream().collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    public Response getUser(@PathParam("id") Long id) {
        User user = User.findById(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        UserDTO userDTO = userService.convertToDTO(user);
        return Response.ok(userDTO).build();
//        return User.findById(id);
    }


    @POST
    @Path("/login")
    public Response getUser(LoginUser loginUser) {
        User user = User.findByEmail(loginUser.email);
        LoggedInUserDTO loggedInUser = new LoggedInUserDTO(user);

        // checkValidPassword(user, loginUser.password)
        // TODO check pw hash -> check for library
        // create user func needs to encrypt pw , this func decrypt
        return Response.ok().entity(loggedInUser).build();
    }

    @POST
    @Transactional
    public Response create(User user) {
        LOG.info(user);
        System.out.println("---------");
        System.out.println(user);
        System.out.println("---------");
        User.persist(user);
        return Response.status(Response.Status.CREATED).entity(user).build();
    }


    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") Long id, User user) {
        User entity = User.findById(id);
        if (entity == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
//        entity.subject = user.subject;
//        entity.description = user.description;
//        entity.courseMaterial = user.courseMaterial;
        entity.persist();
        return Response.ok(entity).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCourse(@PathParam("id") Long id) {
        User entity = User.findById(id);
        if (entity == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        entity.delete();
        return Response.noContent().build();
    }

    @POST
    @Path("/enroll")
    @Transactional
    public Response enrollUser(EnrollmentRequest request) {
        Log.info(request);
        try {
            User enrolledUser = userService.enrollUserToCourse(request.userId, request.courseId);
            Log.info(enrolledUser);
            Log.info(enrolledUser.learningPaths);
            User enrollUser = User.findById(request.userId);
            UserDTO user = new UserDTO(enrollUser);
            return Response.ok(user).build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/course/{courseId}/enrolled-users")
    @Transactional
    public Response getUsersEnrolledInCourse(@PathParam("courseId") Long courseId) {
        Course course = Course.findById(courseId);
        if (course == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Course not found").build();
        }

        Set<User> enrolledUsers = course.users;

        List<UserGetDTO> userDTOs = enrolledUsers.stream()
                .map(UserGetDTO::new)
                .collect(Collectors.toList());

        return Response.ok(userDTOs).build();
    }

    @GET
    @Path("/{id}/courses")
    @Transactional
    public List<CourseGetDTO> getUserCourses(@PathParam("id") Long id) {
//        User u = userService.findUserWithCourses(id);
//        Log.info(u);
        List<CourseGetDTO> courses = userService.findCoursesByUserId(id);
        Log.info(courses);
        return courses;
//        return userService.findCoursesByUserId(id);
    }

    @GET
    @Path("/{id}/coursesq1")
    public Response getUserCoursesq(@PathParam("id") Long userId) {
        try {
            Set<Course> courses = userService.getUserCourses(userId);
            if (courses == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("User with ID " + userId + " not found").build();
            }
            return Response.ok(courses).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error occurred: " + e.getMessage()).build();
        }
    }


}
