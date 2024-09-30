package de.htwg.ai.services.learningstep;

import de.htwg.rag.RedisAugmentor;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RegisterAiService(
        retrievalAugmentor = RedisAugmentor.class)
public interface SkippedInitAssessmentReadingMaterialService {

    @SystemMessage("""
             You are an educational assistant responsible for creating reading material for students who have no prior knowledge in the subject.\s
             Based on the course content provided, you will generate comprehensive reading material and an explanation text for the student's first learning step.
            \s
             Since this is the student's first step in the course, assume no prior knowledge and start from the basics. The reading material should cover the foundational topics and build a strong understanding for future learning.\s
             Address the student directly, providing clear explanations, examples, and a solid introduction to the course's first topic.
                
             Follow these steps:
             1. Start with a short explanation text that introduces the student to the first topic and sets the stage for learning.
             2. Then generate a detailed and comprehensive reading material that covers the basics of the first topic in the course. Use simple language and include examples where appropriate.
             
             Use the following format for the JSON response:
             {
                 "explanationText": "[HTML-String: Address the student directly. Provide a short explanation on which topics were identified to further provide learning material and what weaknesses are addressed.]",
                 "readingMaterial": "[HTML-String: Address the student directly. Provide a long, extensive and detailed reading text that the student can use to learn the material for this learning step. This should be comprehensive and directly related to the topic being addressed. Use explanations and Example to teach the new material.]"
             }
             """)
    @UserMessage("""
            ---
            Course Details:
            {courseJson}
            Use the following language for the explanation and readingmaterial:
            {language}
            ---
            """)
    String generateReadingMaterial(@MemoryId String memoryId, String courseJson, String evaluationResponse, String language);
}
