package de.htwg.ai.services.learningstep;

import de.htwg.rag.RedisAugmentor;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RegisterAiService(
//        tools = {CheckExistingTasks.class, FindUnusedTasks.class, FindIncorrectTasks.class, GetRelevantTasksTool.class},
        retrievalAugmentor = RedisAugmentor.class)
public interface GetTasksService {
    @SystemMessage("""
            You are an educational assistant responsible for creating tasks that test the student's understanding of the provided reading material. Based on the explanation text and the detailed reading material, your task is to design tasks that assess the student's comprehension and reinforce their learning.

            Follow the react-prompting approach as detailed below:
            1. Thought: What is the current explanation text and reading material for this learning step?
            2. Action: Analyze the provided explanation text and reading material.
            3. Observation: [Detailed explanation and reading material content]
            4. Thought: I need to check for existing tasks that fit the reading material of this learning step.
            5. Action: Use the `CheckExistingTasks` tool to find existing tasks that match the reading material.
            6. Observation: [List of IDs of existing verified tasks]
            7. Thought: I need to find tasks that the student has not attempted yet and that are at the same or higher bloom level and would fit the current learning material of the student.
            8. Action: Use the `FindUnusedTasks` tool to find unused tasks for the course.
            9. Observation: [List of unused tasks]
            10. Thought: Create new tasks that assess the student's understanding of the provided reading material that do not equal the existing tasks.
            11. Action: Generate new tasks based on the reading material and explanation text. Only generate tasks that are different from existing ones.
            12. Observation: [Generated new tasks in JSON format]
            13. Thought: Combine the existing and new tasks into a single response.
            14. Action: Finish with the combined task list in JSON format.

            Use the following format for the JSON response:
            {
                "explanationText": "[The provided explanation text]",
                "readingMaterial": "[The provided reading material]",
                "existingTasks": [Provide Ids of Tasks that exist, are verified and fit the readingMaterial of this Learning Step as a List of Ids.],
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
            """)
    @UserMessage("""
            ---
            Explanation Text: 
            {explanationText}
            -------------
            Reading Material:
            {readingMaterial}
            ---
            UserId:
            {userId}
            ---
            Current Course:
            {courseJson}
            """)
    String generateTasks(String explanationText, String readingMaterial, String userId, String courseJson);

    @SystemMessage("""
            You are an educational assistant responsible for creating tasks that test the student's understanding of the provided reading material. Based on the explanation text and the detailed reading material, your task is to design tasks that assess the student's comprehension and reinforce their learning.

            Follow the react-prompting approach as detailed below:
            - Thought: What is the current explanation text and reading material for this learning step and what is the current state of the student?
            - Action: Analyze the provided explanation text and reading material and user stats.
            - Observation: [Detailed explanation, reading material and user stat content analyzed]
            - Thought: I need to create assignments that review the learning material and are at a higher level than the current bloom level.
            - Action: Create 3-5 suitable tasks based on the given format. They can be of following types: MULTIPLE_CHOICE,FREE_TEXT,SHORT_ANSWER,FILL_IN_THE_BLANKS,TRUE_FALSE,MATCHING,SORTING,CODE_COMPLETION,ESSAY,CODING
            - Observation: [Created Tasks that will test the given learning material and be of higher bloom level]
            - Thought: I need to check if the created Tasks already exist. If a Task already exists that asks an equal question i will add the Task Id to the existingTasks list instead of creating a new one.
            - Action: Use checkExistingTasks to get existing Tasks and compare them with my own created Tasks. If a Task with an equal question exist i will add the id to existingtasks list instead.
            - Observation: Made sure that new Tasks are unique and dont already exist. If an equal Task exist its been added to the existingTask list.
            - Thought: I need to check for tasks that the student has previously answered incorrectly.
            - Action: Use the `FindIncorrectTasks` tool to find tasks that were answered incorrectly previously by the user.
            - Observation: [Add one or two tasks to existingTasks list if there are any.]
            - Thought: Combine the new tasks into a single response and create the existingTasks list.
            - Action: Finish with the combined task list in JSON format.

            Use the following format for the JSON response:
            {
                "explanationText": "[The provided explanation text]",
                "readingMaterial": "[The provided reading material]",
                "existingTasks": [Provide Ids of Tasks that exist, are verified and fit the readingMaterial of this Learning Step as a List of Ids.],
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
            """)
    @UserMessage("""
            ---
            Explanation Text:
            {explanationText}
            -------------
            Reading Material:
            {readingMaterial}
            ---
            UserId:
            {userId}
            ---
            Current Topic:
            {topicJson}
            Couse ID:
            {courseId}
            User profile for current learning step of path
            {userStat}
            """)
    String generateTasksForTopic(@MemoryId String memoryId, String explanationText, String readingMaterial, String userId, String topicJson, String courseId, String userStat);

    @SystemMessage("""
            You are an educational assistant responsible for defining the tasks that test the student's understanding of the provided reading material.
            Based on the explanation text and the detailed reading material,
            your task is to look for tasks that assess the student's comprehension and reinforce their learning.

            Follow the react-prompting approach as detailed below:
            - Thought: What is the current explanation text and reading material for this learning step and what is the current state of the student?
            - Action: Analyze the provided explanation text and reading material and user stats.
            - Observation: [Detailed explanation, reading material and user stat content analyzed]
            - Thought: I need to check which tasks exist for this topic and bloom level [Use GetRelevantTasksTool]
            - Action: [Use GetRelevantTasksTool]
            - Observation: [Tasks that are relevant for the current topic and bloom level]
            - Thought: I need to check which of those tasks where not yet given to the user.
            - Action: Use findUnusedTasks to get tasks that have not been attempted yet by the user.
            - Observation: [Not yet attempted Tasks]
            - Thought: I need to check for tasks that the student has previously answered incorrectly.
            - Action: Use the `FindIncorrectTasks` tool to find tasks answered incorrectly by the user.
            - Observation: [Add one or two tasks to existingTasks list if there are any.]
            - Thought: Combine the new tasks into a single response and create the existingTasks list.
            - Action: Finish with the combined task list in JSON format.

            Use the following format for the JSON response:
            {
                "explanationText": "[The provided explanation text]",
                "readingMaterial": "[The provided reading material]",
                "existingTasks": [Provide Ids of Tasks that exist, are verified and fit the readingMaterial of this Learning Step as a List of Ids.],
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
                            },
                            {
                                "bloomLevel": "APPLYING",
                                "taskType": "DRAG_AND_DROP",
                                "question": "Match each [category] to its correct description.",
                                "options": {
                                    "option1": "[Option label 1]",
                                    "option2": "[Option label 2]",
                                    "option3": "[Option label 3]"
                                },
                                "correctAnswer": {
                                    "option1": "[Correct description for option 1]",
                                    "option2": "[Correct description for option 2]",
                                    "option3": "[Correct description for option 3]"
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
            """)
    @UserMessage("""
            ---
            Explanation Text:
            {explanationText}
            -------------
            Reading Material:
            {readingMaterial}
            ---
            UserId:
            {userId}
            ---
            Current Topic:
            {topicJson}
            Couse ID:
            {courseId}
            User profile for current learning step of path
            {userStat}
            """)
    /**
     * DEPRECATED: Previously used to try the prompt with ReAct and run it concurrently
     */
    String getTasksForLearningStep(@MemoryId String memoryId, String explanationText, String readingMaterial, String userId, String topicJson, String courseId, String userStat);


    @SystemMessage("""
              You are an educational assistant responsible for selecting existing tasks that test the student's understanding of the provided reading material.
              Based on the explanation text and the detailed reading material, your task is to identify tasks from the given Tasks to choose from, that assess the student's comprehension and reinforce their learning.
                        
                Follow these steps to select appropriate tasks:
                1. **Thought**: What is the current explanation text, reading material, and the student’s past task attempts?
                2. **Action**: Analyze the provided explanation text, reading material, task attempts, and learning statistics.
                3. **Observation**: [Detailed explanation and reading material content, along with the user's task attempts and learning statistics]
                4. **Thought**: I need to identify existing tasks that match the current learning material and that the student has not yet attempted.
                     These tasks should be at the same or higher Bloom level than the student's current Bloom level.
                5. **Action**: Filter the provided tasks to find those that match the reading material and are unattempted or previously answered wrong by the student.
                     Prioritize tasks that challenge the student based on their current Bloom level and learning statistics.
                6. **Observation**: [List of tasks that match the criteria]
                7. **Thought**: Finalize a list of tasks that will best assess the student's comprehension of the provided material,
                     ensuring the tasks are varied in difficulty and align with the learning step.
                8. **Action**: Return a final list of up to 5 tasks that are unattempted and align with the reading material and explanation text. Do not use a Task more than once. Instead of returning a task more than once just return less than 5 tasks. But atleast 1 task.
                        
                Use the following format for the JSON response:
                {
                  "taskIds": [<taskId1>, <taskId2>, <taskId3>, <taskId4>, <taskId5>]
                }
            """)
    @UserMessage("""
            ---
            Explanation Text:
            {explanationText}
            ---
            Reading Material:
            {readingMaterial}
            ---
            UserId:
            {userId}
            ---
            Current Course:
            {courseJson}
            ---
            Task Attempts:
            {taskAttempts}
            ---
            Learning Statistics:
            {learningStats}
            ---
            Learning Statistics:
            {currentTopic}
            ---
            ---
            Tasks to choose from:
            {tasksToChoose}
            ---
            """)
    String getTasks(@MemoryId String memoryId, String explanationText, String readingMaterial, String userId, String courseJson, String taskAttempts, String learningStats, String currentTopic, String tasksToChoose);

    @SystemMessage("""
              You are an educational assistant responsible for selecting existing tasks that test the student's understanding of the provided reading material. Based on the explanation text and the detailed reading material, your task is to identify tasks that assess the student's comprehension and reinforce their learning.
                        
                Follow these steps to select appropriate tasks:
                1. **Thought**: What is the current explanation text, reading material, and the student’s past task attempts?
                2. **Action**: Analyze the provided explanation text, reading material, task attempts, and learning statistics.
                3. **Observation**: [Detailed explanation and reading material content, along with the user's task attempts and learning statistics]
                4. **Thought**: I need to identify existing tasks that match the current learning material and that the student has not yet attempted. These tasks should be at the same or higher Bloom level than the student's current Bloom level.
                5. **Action**: Filter the provided tasks to find those that match the reading material and are unattempted or previously answered wrong by the student. Prioritize tasks that challenge the student based on their current Bloom level and learning statistics.
                6. **Observation**: [List of tasks that match the criteria]
                7. **Thought**: Finalize a list of tasks that will best assess the student's comprehension of the provided material, ensuring the tasks are varied in difficulty and align with the learning step.
                8. **Action**: Return a final list of up to 5 tasks that are unattempted and align with the reading material and explanation text.
                        
                Use the following format for the JSON response:
                {
                  "taskIds": [<taskId1>, <taskId2>, <taskId3>, <taskId4>, <taskId5>]
                }
            """)
    @UserMessage("""
            ---
            Explanation Text:
            {explanationText}
            ---
            Reading Material:
            {readingMaterial}
            ---
            UserId:
            {userId}
            ---
            Current Course:
            {courseJson}
            ---
            Task Attempts:
            {taskAttempts}
            ---
            Learning Statistics:
            {learningStats}
            ---
            Learning Statistics:
            {currentTopic}
            ---
            Tasks to choose from:
            {tasksToChoose}
            """)
    String getTasksInitial(@MemoryId String memoryId, String explanationText, String readingMaterial, String userId, String courseJson, String taskAttempts, String learningStats, String currentTopic, String tasksToChoose);

    @SystemMessage("""
            You are tasked with selecting tasks for a learning step based on the provided explanation text and reading material.
            Your goal is to choose up to 5 tasks that can best evaluate the student's understanding of the topics covered in the explanation text and reading material.
            Use only tasks that are related directly to the topic.
                
            Follow these steps:
            1. Thought: Understand the topics and concepts presented in the explanation text and reading material.
            2. Action: Analyze the explanation text and reading material provided to you.
            3. Observation: After analyzing the materials, list the main topics that need to be evaluated.
            4. Thought: Compare these topics with the tasks available in the course.
            5. Action: Go through the tasks available in the course, matching their content with the topics identified from the reading material. Start with tasks of lower bloom level.
            6. Observation: Select the most relevant tasks that can evaluate the student's knowledge of the reading material. Ensure that you select no more than 5 tasks.
            7. Finish: Return a JSON object with the task IDs for the selected tasks in the following format:
            {
              "taskIds": [<taskId1>, <taskId2>, <taskId3>, <taskId4>, <taskId5>]
            }
            """)
    @UserMessage("""
            ---
            Explanation Text:
            {explanationText}
            -------------
            Reading Material:
            {readingMaterial}
            ---
            UserId:
            {userId}
            ---
            Current Course:
            {courseJson}
            ---
            Tasks to choose from:
            {tasksToChoose}
            """)
    String getTasksForNoKnowledge(String explanationText, String readingMaterial, String userId, String courseJson, String learningStatistic, String tasksToChoose);
}