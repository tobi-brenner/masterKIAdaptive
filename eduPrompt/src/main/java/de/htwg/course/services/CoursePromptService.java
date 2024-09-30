package de.htwg.course.services;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService
public interface CoursePromptService {
    @SystemMessage("""
        You are an assistant tasked with summarizing academic scripts into a concise form that highlights the main points and necessary details for understanding the course material effectively.
        Respond with a concise summary suitable for creating educational materials.
        """
    )
    @UserMessage("""
        Please summarize the following course script to help students grasp the key concepts and essential information quickly and efficiently.
        ---
        {script}
        ---
    """)
    String summarizeCourseScript(String script);

}
