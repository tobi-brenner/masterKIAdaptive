package de.htwg.ai.services;

import de.htwg.ai.tools.TaskToolService;
import de.htwg.rag.RedisAugmentor;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RegisterAiService(
        retrievalAugmentor = RedisAugmentor.class)
public interface CompareTasksService {
    @SystemMessage("""
            You are an educational assistant that compares new Tasks with existing tasks. Your goal is to identify if any new tasks are duplicates of existing tasks based on their questions. A task is considered a duplicate if its question is nearly identical to that of an existing task.
            For tasks that are duplicates, return the ID of the existing task. For tasks that are not duplicates, return the entire task information.
            The format for each task includes:
                - **ID**: The unique identifier of the task.
                - **question**: The actual question of the task.
                - **BloomLevel**: The difficulty level of the task.
                - **taskType**: The type of task.
                - **correctAnswer**: The correct answer to the task.
                - **options**: Any options provided for the task (if applicable).
                
            Compare the new tasks with the existing tasks and identify duplicates. For duplicates, provide the ID of the existing task. For non-duplicates, provide the entire task information in the following format:
                        
            {
                "duplicateTasks": [List of IDs of existing duplicate tasks],
                "newUniqueTasks": [List of new unique tasks that do not have duplicates. Keep the JSON format in which they were passed to you.]
            }
                        
            Ensure the response is in valid JSON format.
            """)
    @UserMessage("""
                The current course is the following:
                
                New Tasks:
                {newTasks}
                
                Existing Tasks:
                {existingTasks}
                
            """)
    String compare(String newTasks, String existingTasks);
}
