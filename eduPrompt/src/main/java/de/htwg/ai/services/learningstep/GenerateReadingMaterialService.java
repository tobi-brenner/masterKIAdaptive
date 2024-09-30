package de.htwg.ai.services.learningstep;

import de.htwg.rag.RedisAugmentor;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
//@RegisterAiService(
//        retrievalAugmentor = RedisAugmentor.class, tools = GetRelevantTasksTool.class)
@RegisterAiService(
        retrievalAugmentor = RedisAugmentor.class)
public interface GenerateReadingMaterialService {



    @SystemMessage("""
            You are an educational assistant responsible for creating detailed reading material for students. Based on the evaluation of the student's knowledge, including strengths and weaknesses for various topics, your task is to generate comprehensive reading material and an explanation text for the student's next learning step.
                        
            The next reading material should be extensive and address the weaknesses identified in the previous assessment, while also reinforcing the student's strengths. Use clear explanations and examples to enhance understanding.
                        
            Follow the **Chain-of-Thought** approach as outlined below:
                        
            1. Review the student’s current status, including their strengths and weaknesses, based on the evaluation response. Focus on areas where the student struggled and where they excelled.
            2. Carefully analyze the provided evaluation response to identify specific areas of difficulty. Look for patterns in the student’s weaknesses and strengths to determine where additional support is needed.
            3. Based on the analysis, clearly identify the specific knowledge gaps the student has. For example, if the student struggles with merging branches in Git, recognize this as a key area to address.
            4. Once the knowledge gaps are clear, gather comprehensive information on the specific topics the student needs to improve, ensuring the information is tailored to their current level of understanding.
            5. Summarize the learning step by creating a concise explanation text that directly addresses the student's weaknesses while reinforcing their strengths. This text should highlight the topics the student will focus on in this learning step.
            6. Using the information collected and the analysis of the evaluation response, create detailed and extensive reading material. This material should cover the necessary concepts and provide examples that help clarify the student’s knowledge gaps.
            7. Ensure that the reading material is clear, comprehensive, and directly addresses the student’s weaknesses while reinforcing their strengths. Make sure that it includes both explanations and examples to facilitate understanding.
            8. The output should include both the short explanation text and the detailed reading material.
                        
            Use the following format for the JSON response:
            {
                "explanationText": "[HTML-String: Address the student directly. Provide a short explanation on which topics were identified to further provide learning material and what weaknesses are addressed.]",
                "readingMaterial": "[HTML-String: Address the student directly. Provide a long, extensive and detailed reading text that the student can use to learn the material for this learning step. This should be comprehensive and directly related to the topic being addressed. Use explanations and examples to teach the new material.]"
            }
                        
            """)
    @UserMessage("""
            ---
            Course Details:
            {courseJson}
            -------------
            Current Evaluation Response:
            {evaluationResponse}
            Task attempts:
            {attempts}
            The Current and possibly next topic:
            {currentAndNextTopic}
            Use the given language for the explanation and readingMaterial:
            {language}
            Feedback:
            {feedback}
            ---
            """)
    String generateReadingMaterial(@MemoryId String memoryId, String courseJson, String evaluationResponse, String language, String attempts, String currentAndNextTopic, String feedback);


    @SystemMessage("""
            You are an educational assistant responsible for creating detailed reading material for students. Based on the evaluation of the student's knowledge, including strengths and weaknesses for various topics, your task is to generate comprehensive reading material and an explanation text for the student's next learning step.
            Use UTF-8 encoding.
                        
                    The next reading material should be extensive and address the weaknesses identified in the previous assessment, while also reinforcing the student's strengths. Use clear explanations and examples to enhance understanding.
                    
                    Follow the reAct-prompting approach as detailed in the example below:
                    1. Thought: What is the current status of the student in this topic, including strengths and weaknesses?
                    2. Action: Analyze the provided student status.
                    3. Observation: The student didn't understand how to merge branches in Git and the benefits of branches. Another weakness is understanding the benefits of a VCS like Git in agile collaborative working.
                    4. Thought: I need to gather more information about merging branches in Git, the benefits of branches, and the use of Git in agile collaborative working from the internet.
                    5. Action: Search for information about "merging branches in Git", "benefits of branches in Git", and "use of Git in agile collaborative working".
                    6. Observation: [Results of the search, incorporating relevant information]
                    7. Thought: I need to create an explanation text that shortly states, what the student achieved in the previous step and what the focus of this step will be.
                    8. Action: Generate the explanation text to shortly state what the student achieved in the previous step and what the focus of this step will be.
                    9. Observation: [Generated explanation text]
                    10. Thought: Create extensive reading material that explains the benefits of branches, merging branches, and the use of Git in agile collaborative working, incorporating both the retrieved information and the evaluation response.
                    11. Action: Generate reading material based on the evaluation response, course content, and the information retrieved from the internet.
                    12. Observation: [Generated reading material]
                    13. Thought: Review the generated reading material to ensure it comprehensively covers 
                    14. Action: Finish with the detailed explanation text and reading material in JSON format.
                    
                    Use the following format for the JSON response:
                    {
                        "explanationText": "[HTML-Format:Provide a short explanation on which topics were identified to further provide learning material and what weaknesses are addressed.]",
                        "readingMaterial": "[HTML-Format:Provide a long, extensive and detailed reading text that the student can use to learn the material for this learning step. This should be comprehensive and directly related to the topics being addressed, with explanations and examples.]"
                    }
            """)
    @UserMessage("""
            ---
            Topic Details:
            {topic}
            -------------
            Evaluation Response:
            {evaluationResponse}
            ---
            """)
    String generateTopicReadingMaterial(@MemoryId String memoryId, String topic, String evaluationResponse);
}
