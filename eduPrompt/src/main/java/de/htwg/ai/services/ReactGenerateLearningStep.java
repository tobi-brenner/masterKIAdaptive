package de.htwg.ai.services;

import de.htwg.ai.tools.TaskToolService;
import de.htwg.rag.RedisAugmentor;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RegisterAiService(tools = {TaskToolService.class},
        retrievalAugmentor = RedisAugmentor.class)
public interface ReactGenerateLearningStep {
    @SystemMessage("""
            Given the user's current status, strengths, and weaknesses in the current learning course, provide fitting learning material and tasks for the next learning step.
            Do only use existing provided Tools: Available: compareNewWithExistingTasks.

            Thought: What is the current status of the student in this course and in which areas does the student have weaknesses?
            Action: Analyze provided student status.
            Observation: The Student didn't understand how to merge branches in git and what the benefits of branches are. Another weakness the student needs to address is the benefit of a VCS like git in agile collaborative working.

            Thought: I need to create extensive reading material that explains the benefits of branches and merging branches. Also about the benefit of git in agile collaborative working.
            Action: Create reading material based on retrieval augmentor.
                    Observation: [generated reading material]

            Thought: I need to create tasks which check if the User understood this reading material.
            Act: Create tasks which check the reading materials content in the given JSON format.
                    Observation: [List of generated Tasks in JSON format]

            Thought: I need to compare these new tasks with the already existing tasks of the course. The existing task IDs will be added to the existingTask array, and the new tasks will be within the topics.
            Action: [compareNewWithExistingTasks newTasks courseId]
            Observation: {
                "duplicateTasks": [List of IDs of existing duplicate tasks],
                "newUniqueTasks": [List of new unique tasks that do not have duplicates. Keep the JSON format in which they were passed to you.]
            }

            Thought: I need to give the Learning Step as JSON response.
            Action: Finish example: [
            {
                "existingTasks": [Provide IDs of tasks that exist, are verified, and fit the readingMaterial of this Learning Step as a List of IDs.],
                "information": "[Provide a detailed explanation of the course materials that the student should focus on. Address the weaknesses and reinforce the strengths identified in the previous assessment.]",
                    "readingMaterial": "[Provide a long and detailed reading text that the student can use to learn the material for this learning step. This should be comprehensive and directly related to the topics being addressed, with explanations and examples.]",
                    "topics": [
                {
                    "topicName": "topicname1",
                        "topicId": 1,
                        "tasks": [
                    {
                        "bloomLevel": "REMEMBERING",
                            "taskType": "MULTIPLE_CHOICE",
                            "question": "What is concurrent programming?",
                            "options": {
                        "option1": "Writing programs that are structured into tasks that can or should be executed in parallel",
                                "option2": "Writing programs that are executed on a single core hardware",
                                "option3": "Writing programs that are strictly sequential in execution"
                    },
                        "correctAnswer": {
                        "option1": "Writing programs that are structured into tasks that can or should be executed in parallel"
                    }
                    },
                    {
                        "bloomLevel": "UNDERSTANDING",
                            "taskType": "FREE_TEXT",
                            "question": "Give an example of a real-world application that requires concurrent programming.",
                            "correctAnswer": {
                        "text": "A web app that needs to stay responsive to user input while concurrently rendering 3D animations."
                    }
                    }
                                    ]
                },
                {
                    "topicName": "topicname2",
                        "topicId": 2,
                        "tasks": [
                    {
                        "bloomLevel": "REMEMBERING",
                            "taskType": "MULTIPLE_CHOICE",
                            "question": "What is concurrent programming?",
                            "options": {
                        "option1": "Writing programs that are structured into tasks that can or should be executed in parallel",
                                "option2": "Writing programs that are executed on a single core hardware",
                                "option3": "Writing programs that are strictly sequential in execution"
                    },
                        "correctAnswer": {
                        "option1": "Writing programs that are structured into tasks that can or should be executed in parallel"
                    }
                    },
                    {
                        "bloomLevel": "UNDERSTANDING",
                            "taskType": "FREE_TEXT",
                            "question": "Give an example of a real-world application that requires concurrent programming.",
                            "correctAnswer": {
                        "text": "A web app that needs to stay responsive to user input while concurrently rendering 3D animations."
                    }
                    }
                                    ]
                }
                            ]
            }
                    ]
                    """)
    @UserMessage("""
                ---
                
                The current course is the following: 
                {courseJson}
                -------------
                Students current status:
                {prompt}
                ---
                
            """)
    String generateNextStep(@MemoryId String memoryId, String prompt, String courseJson);
}
