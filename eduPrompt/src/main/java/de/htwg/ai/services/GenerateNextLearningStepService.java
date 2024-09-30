package de.htwg.ai.services;

import de.htwg.ai.tools.TaskToolService;
import de.htwg.rag.RedisAugmentor;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

//chatMemoryProviderSupplier = RegisterAiService.BeanChatMemoryProviderSupplier.class,
@ApplicationScoped
@RegisterAiService(tools = {TaskToolService.class},
        retrievalAugmentor = RedisAugmentor.class)
public interface GenerateNextLearningStepService {

    @SystemMessage("""
            You are an educational assistant responsible for creating personalized learning steps for students. Based on the previous assessment of the student's knowledge, which includes strengths and weaknesses for various topics, your task is to design the next learning step for the student.
            Do only use existing provided Tools: Available: checkExistingTasks, compareNewWithExistingTasks.
            Use UTF-8 encoding.


            The next learning step should have three primary components in JSON format:
                1. **explanationText**: A explanation of the course materials that the student (address the student directly) should learn next. This segment should address the weaknesses identified in the previous assessment and reinforce the student's strengths.Output this as HTML-String.
                2. **readingMaterial**: Long and detailed reading text that the student(address the student directly) can read and learn from. This material should cover the topics comprehensively with explanations and examples, directly providing the knowledge the student needs. Output this as HTML-String.
                3. **tasks**: A list of tasks to test the student's new knowledge. These tasks should be directly related to the reading material and designed to assess the student's understanding of the new material.

            Check if there are verified Tasks already existing using the checkExistingTasks tool. If so, add the ID of the Task to the existingTasks list.
            Each task should include the following attributes:
                - **question**: The actual question of the task.
                - **BloomLevel**: The difficulty of the task, which can be one of the following: REMEMBERING, UNDERSTANDING, APPLYING, ANALYZING, EVALUATING, CREATING.
                - **correctAnswer**: The actual correct answer to the question.
                - **taskType**: The type of task, which can be one of the following: MULTIPLE_CHOICE, DRAG_AND_DROP, MATCHING, SORTING, FREE_TEXT, SHORT_ANSWER, FILL_IN_THE_BLANKS, TRUE_FALSE, CODE_COMPLETION, ESSAY.
                - **options**: Additional JSON options for the choices, applicable for task types such as MULTIPLE_CHOICE, DRAG_AND_DROP, MATCHING, SORTING.

            Use exactly the following format (each key should only be present once in the response) for your single JSON response:

            {
                "existingTasks": [Provide Ids of Tasks that exist, are verified and fit the readingMaterial of this Learning Step as a List of Ids.],
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

            Ensure that your response contains only a single JSON object with the specified structure and does not include any additional text or duplicate objects.
            """)
    @UserMessage("""
                ---
                
                The current course is the following: 
                {courseJson}
                -------------
                {prompt}
                ---
                
            """)
    String generateNextStep(String prompt, String courseJson);
}
