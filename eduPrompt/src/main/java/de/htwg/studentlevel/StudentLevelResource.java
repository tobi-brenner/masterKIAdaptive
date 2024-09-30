package de.htwg.studentlevel;

import dev.langchain4j.service.MemoryId;
import io.quarkiverse.langchain4j.ChatMemoryRemover;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestQuery;

import java.util.List;

@Path("/studentlevel")
public class StudentLevelResource {

    private static final Logger LOG = Logger.getLogger(StudentLevelResource.class);
    @Inject
    StudentLevelPromptService studentLevelPromptService;

    public record Examination(String topicName, List<Question> questions, List<Answer> answers) {
    }

    public record Question(int id, String question) {
    }

    public record Answer(int id, String answer) {
    }


    // mb GET insterad of POST
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
//    public StudentLevel determineLevel(Examination examination) {
    public String determineLevel(Examination examination) {
//        if (examination == null || examination.topicName() == null || examination.questions() == null || examination.answers() == null) {
//            throw new IllegalArgumentException("Missing data in examination input");
//        }
        System.out.println("-------------------");
        System.out.println(examination);
        System.out.println(String.valueOf(examination));
        System.out.println("-------------------");

        String memoryId = "1";
        String prompt = formatPrompt(examination);

        System.out.println("-------------------");
        System.out.println(prompt);
        System.out.println("-------------------");

        return studentLevelPromptService.determineLevel(memoryId, prompt);

        // TODO
        // Pass more data here. Userid, courseid and users answers/questions
        // Make db queries to get course material (probably redis vector store) and solution to init tasks
        // pass those information to the LLM/Prompt to get a better level

        // MemoryId, use either UserId or store memoryID in db
        // TODO: need to check if the userID can be used for each different AI-service as unique memoryId
        // If not store a unique memoryID for each AI-Service
    }

    private String formatPrompt(Examination examination) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Topic: ").append(examination.topicName()).append("\n");

        for (int i = 0; i < examination.questions().size(); i++) {
            Question question = examination.questions().get(i);
            Answer answer = examination.answers().stream()
                    .filter(a -> a.id() == question.id())
                    .findFirst()
                    .orElse(new Answer(question.id(), "No answer provided"));

            promptBuilder.append("Q").append(question.id()).append(": ").append(question.question())
                    .append("\nA").append(answer.id()).append(": ").append(answer.answer())
                    .append("\n\n");
        }

        return promptBuilder.toString();
    }


    /*
     * Removes long memory intensive chat memory by memoryId(mb userID)
     * This may not be a rest endpoint later on
     * Could store conversation memory in database instead of in memory
     */
    @DELETE
    @Path("/conversation")
    public void deleteConversation(@RestQuery String memoryId) {
        ChatMemoryRemover.remove(studentLevelPromptService, memoryId);
    }
}
