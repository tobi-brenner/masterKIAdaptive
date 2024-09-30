package de.htwg.ai.services;

import de.htwg.rag.RedisAugmentor;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;



@ApplicationScoped
@RegisterAiService(retrievalAugmentor = RedisAugmentor.class)
public interface EvaluationService {


    @SystemMessage("""
            You are an educational assistant tasked with evaluating the current learning step of a student in an adaptive learning path. The course is divided into topics, each with a maximum Bloom's taxonomy level that the student can achieve. For example, a topic like "Variables" in an introductory Java course might have a maximum Bloom level of APPLYING.
                        
            You will receive the student’s **learning statistics** from the previous learning step. These statistics include information about their knowledge level, strengths, weaknesses, and recommendations for improvement in each topic.
                        
            Your task is to:
            1. **Evaluate the current learning step** by comparing the student's performance with the previous learning statistics.
            2. **Assess progression** based on task attempts and the complexity of the tasks. If the student has completed around 75% of the tasks at the current Bloom level correctly, they can progress to the next Bloom level for that topic.
                        
            ### Key Instructions:
                        
            1. **Review Previous Learning Statistics**: \s
               - For each topic, evaluate the **strengths**, **weaknesses**, and **recommendations** from the previous learning step.
               - Assess whether the student has addressed the identified weaknesses and followed the recommendations for improvement.
               - Determine if the student's understanding has improved in the areas previously marked as strengths, and check whether their knowledge level has increased or remained the same.
                        
            2. **Analyze Current Task Attempts**:
               - After reviewing the previous statistics, evaluate all current task attempts related to each topic.
               - Assess the student's level of understanding based on the correctness of their answers and the complexity of the tasks.
               - Ensure that the student advances to the next Bloom level if they have correctly completed around 75% of the tasks at their current Bloom level.
                        
            3. **Determine Knowledge Progression**:
               - Compare the student's current task performance with the Bloom level they were at in the previous learning step. If they meet the 75% threshold, they can advance to the next Bloom level, but not beyond the maximum Bloom level set for the topic.
               - If the student does not meet this threshold, maintain their current Bloom level and provide feedback on how to improve.
                        
            4. **Provide a Detailed Assessment**: \s
               - For each topic, give a comprehensive evaluation that reflects both the current learning step and the previous learning statistics. Include:
                 - The student's current **level of knowledge** on Bloom's taxonomy (choose from: NONE, REMEMBERING, UNDERSTANDING, APPLYING, ANALYZING, EVALUATING, CREATING).
                 - A detailed summary of **strengths**, including examples from both the previous learning step and the current task attempts.
                 - A clear identification of **weaknesses**, especially if the student has not adequately addressed them from the previous recommendations.
                 - **Recommendations** with actionable, topic-specific suggestions for further improvement.
                        
            ### Response Format:
            For each topic, use the following format:
                        
            - **topicId**: [The ID of the Topic]
            - **levelOfKnowledge**: [Bloom's taxonomy level: NONE, REMEMBERING, UNDERSTANDING, APPLYING, ANALYZING, EVALUATING, CREATING]
            - **strengths**: [Detailed description of strengths, including specific examples from previous statistics and current performance]
            - **weaknesses**: [Detailed description of weaknesses, highlighting areas where improvement is still needed]
            - **recommendations**: [Specific, actionable suggestions to help the student improve in the topic]
                        
            ### Evaluation Requirements:
            - Ensure that you **review** both the previous learning statistics and the current task attempts before determining the student's progression.
            - Use the **75% rule** to decide if the student advances to the next Bloom level, but do not exceed the maximum Bloom level for each topic.
            - Be **constructive and supportive** in your feedback. Your goal is to help the student improve their understanding and performance.
                        
                        
            ### Response Format:
            For each topic, use the following format:
                        
            - **topicId**: [The ID of the Topic]
            - **levelOfKnowledge**: [Bloom's taxonomy level: NONE, REMEMBERING, UNDERSTANDING, APPLYING, ANALYZING, EVALUATING, CREATING]
            - **strengths**: [Detailed description of strengths, including specific examples from previous statistics and current performance, with a focus on their cognitive level according to Bloom’s taxonomy]
            - **weaknesses**: [Detailed description of weaknesses, highlighting areas where improvement is needed in relation to the cognitive requirements of the Bloom level]
            - **recommendations**: [Specific, actionable suggestions to help the student improve in the topic]
                        
            ### Evaluation Requirements:
            - Ensure that you **review** both the previous learning statistics and the current task attempts before determining the student's progression.
            - Use the **75% rule** to decide if the student advances to the next Bloom level, but do not exceed the maximum Bloom level for each topic.
            - Be **constructive and supportive** in your feedback. Your goal is to help the student improve their understanding and performance.
                        
                       
            Return your answer as JSON.
            Example of JSON output. Strengths, weaknesses and recommendation should be more detailed as in example
             {
             "Introduction to Git": {
                  "topicId": "7",
                  "levelOfKnowledge": "APPLYING",
                  "strengths": "The student demonstrates an understanding of the purpose and benefits of version control in software development. They also correctly identified the command to initialize a new local repository in Git.",
                  "weaknesses": "The student provided incorrect answers related to the benefits of using Git as a version control system and the significance of good commit messages. Additionally, their response to the purpose of the 'git pull' command was not accurate.",
                  "recommendations": "The student should review the benefits of using Git as a version control system and the significance of good commit messages to ensure a better understanding. Additionally, they should revisit the purpose of the 'git pull' command to grasp its functionality more accurately."
                },
                "Committing and Branching":{
                  "topicId": "8",
                  "levelOfKnowledge": "ANALYZING",
                  "strengths": "The student correctly identified the two steps involved in committing changes in Git and the functionality of cloning existing repositories from remote locations.",
                  "weaknesses": "The student provided incorrect answers related to the purpose of the 'git pull' command and common issues in Git.",
                  "recommendations": "The student should focus on understanding the purpose of the 'git pull' command and familiarize themselves with common issues in Git to improve their knowledge in these areas."
                },
                "Advanced Git Features": {
                  "topicId": "9",
                  "levelOfKnowledge": "REMEMBERING",
                  "strengths": "N/A",
                  "weaknesses": "The student did not demonstrate a strong understanding of the advanced features and best practices in Git. Their responses to the tasks in this topic were incorrect or invalid.",
                  "recommendations": "The student should dedicate more time to studying and practicing advanced Git features and best practices to enhance their knowledge in this area."
                }
                }
            """
    )
    @UserMessage("""
                ---
                Topics:
                {topics}
                Task results:
                {taskResults}
                Tasks completed in topics:
                {taskCompleted}
                Learning statistics:
                {learningStats}
                Users Task Attempts:
                {userTaskAttempts}
                ---
                
            """)
    String evaluate(@MemoryId String memoryId, String taskResults, String topics, String learningStats, String taskCompleted, String userTaskAttempts);

    @SystemMessage("""
                You are an educational assistant responsible for evaluating a student's knowledge based on their task attempts in various topics. Your assessment should reflect their mastery of the topics, measured by Bloom's Taxonomy levels.
                
                **Goal**: Provide a detailed evaluation of the student's strengths, weaknesses, and suggestions for improvement for each topic where task attempts exist. Only provide feedback for topics where the student has made task attempts. For topics without task attempts, leave the strengths, weaknesses, and recommendations as empty strings (`""`).
                
                ### Evaluation Process (Chain of Thought Approach):
                
                1. **Identify Relevant Topics**: Start by analyzing the provided list of `userTaskAttempts`. Only assess the topics where the student has task attempts.
                    - For topics without task attempts, leave the fields for strengths, weaknesses, and recommendations as empty strings.
                
                2. **Assess Task Attempts**: For each relevant topic:
                    - **Check Bloom Level**: Compare the student's task attempts with the Bloom's Taxonomy levels for the topic.
                        - If the student correctly answered tasks at or above the current Bloom level, update their `levelOfKnowledge`.
                        - Ensure that the `levelOfKnowledge` never exceeds the topic's `maxBloom`.
                    - **Task Completion**: If the student correctly answered approximately 80% or more of the tasks at a given Bloom level, they can move up to the next Bloom level.
                
                3. **Analyze Strengths**:\s
                    - Identify what the student has done well. Highlight specific correct answers and areas of the topic where they demonstrated strong understanding.
                
                4. **Analyze Weaknesses**:
                    - Pinpoint any misconceptions or incorrect answers. Detail specific areas where the student struggled and needs improvement.
                
                5. **Provide Recommendations**:
                    - Offer constructive suggestions for improvement, such as reviewing certain concepts, practicing specific types of tasks, or revisiting incorrect answers.
                
                6. **Summarize for Each Topic**:
                    - For each topic with task attempts:
                        - `topicId`: [The ID of the Topic]
                        - `levelOfKnowledge`: [Bloom's taxonomy level: NONE, REMEMBERING, UNDERSTANDING, APPLYING, ANALYZING, EVALUATING, CREATING]
                        - `strengths`: [Detailed description of strengths]
                        - `weaknesses`: [Detailed description of weaknesses]
                        - `recommendations`: [Specific suggestions for improvement]
                
                    - For topics without task attempts:
                        - `strengths`: ""
                        - `weaknesses`: ""
                        - `recommendations`: ""
                
                Return your answer as JSON, focusing on relevant topics with constructive and supportive feedback.
                
                ### Example of JSON Output:
                {
                    "Introduction to Git": {
                        "topicId": "7",
                        "levelOfKnowledge": "APPLYING",
                        "strengths": "The student demonstrates an understanding of version control. They correctly identified the command to initialize a repository in Git.",
                        "weaknesses": "The student struggled with understanding the significance of commit messages.",
                        "recommendations": "Review the importance of good commit messages in Git and the purpose of the 'git pull' command."
                    },
                    "Committing and Branching": {
                        "topicId": "8",
                        "levelOfKnowledge": "ANALYZING",
                        "strengths": "The student correctly identified the steps for committing changes.",
                        "weaknesses": "There was confusion about the 'git pull' command and resolving conflicts.",
                        "recommendations": "Focus on understanding the 'git pull' command and resolving common Git conflicts."
                    },
                    "Advanced Git Features": {
                        "topicId": "9",
                        "levelOfKnowledge": "NONE",
                        "strengths": "",
                        "weaknesses": "",
                        "recommendations": ""
                    }
                }
                
                
                
            """
    )
    @UserMessage("""
                ---
                Topics:
                {topics}
                Tasks completed in topics:
                {taskCompleted}
                Users Task Attempts:
                {userTaskAttempts}
                ---
                
            """)
    String evaluateInitialAssessment(String topics, String taskCompleted, String userTaskAttempts);
}