package de.htwg.ai.services;

import de.htwg.ai.tools.GetRelevantTasksTool;
import de.htwg.ai.tools.GetTasksOfTopic;
import de.htwg.ai.tools.TaskToolService;
import de.htwg.rag.RedisAugmentor;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RegisterAiService(retrievalAugmentor = RedisAugmentor.class)
//@RegisterAiService(retrievalAugmentor = RedisAugmentor.class, tools = GetTasksOfTopic.class)
public interface InitialAssessmentGenerationService {
    /**
     * @param course
     * @param language
     * @return Initial course evaluation
     */
    @SystemMessage("""
                You are tasked with selecting tasks for an initial assessment from a list of existing tasks for each topic provided within a course structure.
                
                Your goal is to select **a total of 7 tasks** across the first topics in the course. The first 2 topics can have **3-4 tasks each**, the third topic can have **1-3 tasks**, you should stop selecting tasks, even if there are more topics left.
                
                The tasks already exist within the provided course structure. You do not need to create any new tasks or fetch tasks from any external tool.
                
                Follow these steps using the to determine the best tasks:
                
                1. **Thought**: Start by analyzing the topic's `maxBloom` level. This indicates the highest cognitive level you should aim for when selecting tasks.
                    - If the `maxBloom` is APPLYING, focus only on tasks up to APPLYING (avoid higher-level tasks like EVALUATING or CREATING).
                    - If the `maxBloom` is EVALUATING, include tasks only up to that level.
                
                2. **Action**: Look at the tasks already provided for each topic within the course structure.
                    - **Limit**:\s
                      - For the **first 2 topics**, select **3-4 tasks** each.
                      - For **subsequent topics**, select **up to 2 tasks** each.
                      - Once you reach a total of **7 tasks** across all topics, stop selecting tasks.
                    - Aim to select tasks from both lower Bloom's levels (REMEMBERING, UNDERSTANDING) and mid-to-high Bloom's levels (APPLYING, ANALYZING, EVALUATING, CREATING), depending on the topic's `maxBloom`.
                
                3. **Observation**: Once you’ve reviewed the tasks for each topic, choose the most suitable tasks that span a range of Bloom’s levels. Ensure that the selected tasks are varied in difficulty.
                
                4. **Thought**: Carefully assess the tasks to ensure they represent the range of difficulty and cognitive levels. Remember, the assessment should help categorize the student's initial knowledge level.
                
                5. **Action**: Select the task IDs for each topic that best represent the desired levels of Bloom’s taxonomy and list them in the format:
                    {
                        "topicId": <id>,
                        "selectedTaskIds": [<taskId1>, <taskId2>, <taskId3>]
                    }
                
                6. **Repeat**: Perform steps 1-5 for each topic in the provided course structure, keeping track of the total number of tasks selected. Stop once you reach **7 tasks in total**, even if more topics remain.
                
                7. **Finish**: Return the selected task IDs for each topic in the following JSON format:
                {
                    "topics": [
                        {
                            "topicId": 1,
                            "selectedTaskIds": [12, 24, 125]
                        },
                        {
                            "topicId": 2,
                            "selectedTaskIds": [26, 57, 128]
                        },
                        {
                            "topicId": 3,
                            "selectedTaskIds": [129, 10]
                        }
                    ]
                }
                
                
                
            """)
    @UserMessage("""
                ---
                The course:
                {course}
                ---
                Topics to use:
                {initialTopics}
                ---
                Tasks of the topics:
                {initialTasks}
            """)
    String createInitialCourseAssessment(@MemoryId String memoryId, String course, String language, String initialTopics, String initialTasks);
}