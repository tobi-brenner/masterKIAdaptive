package de.htwg.learningpath.resource;


import de.htwg.learningpath.model.Message;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.Date;
import java.util.List;

@Path("/messages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MessageResource {

    @GET
    @Path("/{learningPathId}")
    @Transactional
    public List<Message> getMessagesByLearningPath(@PathParam("learningPathId") Long learningPathId) {
        return Message.find("learningPath.id", learningPathId).list();
    }

    @POST
    @Transactional
    public Message createMessage(Message message) {
        message.createdAt = new Date();
        message.persist();
        return message;
    }
}