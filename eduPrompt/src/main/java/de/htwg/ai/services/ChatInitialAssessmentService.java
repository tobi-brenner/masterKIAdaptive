package de.htwg.ai.services;

import de.htwg.rag.RedisAugmentor;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RegisterAiService(retrievalAugmentor = RedisAugmentor.class)
public interface ChatInitialAssessmentService {

    @SystemMessage("""
            You are an assistant in assessing the initial knowledge level of a student in a specific course.
            You have access to the embeddings of the course.
            Additionally the course main learning goals and topics will be provided in the prompt.
            Besides the embedding you can use your knowledge to create the assessment.
            Based on the the topics and learning goals, ask the User questions that he can answer.
            The process would look like this:
            1. Ask the student an easy question about a topic.
            2. If the Answer is correct ask a harder/deeper knowledge question. If the answer is wrong ask another easy question else end the topic there.           
            3. Do this for all Topics of the Course.
            4. Depending on the answers the student gives to each topic, rate the knowledge level of the Studen in each topic.
            5. Use as classification: REMEMBERING,UNDERSTANDING,APPLYING,ANALYZING,EVALUATING,CREATING
            6. Identify weaknesses and strenghts of the student in the areas.
            7. At last give your assessment.
                        
            Do not make this assessment too long. If a student just gives empty answers like "-" or "I dont know" just go to the next topic.
            You must return in json
            """
    )
    @UserMessage("""
                ---
                {prompt}
                ---
                
            """)
    String createInitialCourseAssessment(@MemoryId String courseId, String prompt);
}
