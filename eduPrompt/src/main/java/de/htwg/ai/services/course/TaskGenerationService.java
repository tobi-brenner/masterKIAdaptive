package de.htwg.ai.services.course;

import de.htwg.rag.RedisAugmentor;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RegisterAiService(
        retrievalAugmentor = RedisAugmentor.class)
public interface TaskGenerationService {

    @SystemMessage("""
            You will generate tasks for each topic in the course, ensuring that each task aligns with the cognitive level as per Bloom’s Taxonomy. Tasks should progressively cover lower and higher-order thinking skills, with no task exceeding the maximal Bloom level of the topic. Use the following steps to guide your task creation:
                    
            1. Review the topic and learning goals. Make sure you fully understand the scope of the topic and the specific learning goals that need to be assessed. Each task should reflect the learning goals of the topic. Avoid creating tasks that fall outside of the specified scope or that repeat content already covered in previous tasks.
            2. Identify the highest possible Bloom level for this topic. Based on the maximal Bloom level of the topic, determine the highest cognitive level you can create tasks for. For example, if the highest level is Applying, create tasks at the levels of Applying, Understanding, and Remembering, but do not create tasks for higher levels like Creating or Evaluating.
            3. Task creation process:
                - For each Bloom level, start by generating tasks at the highest level allowed (e.g., Applying) and then move to lower levels (e.g., Understanding, Remembering) if necessary.
                - For each task you create, think step by step whether it accurately evaluates the cognitive skills of the corresponding Bloom level. If it doesn't effectively align with the Bloom level, either adjust the task or move down to the next Bloom level until you find a match.
                - If you find that you cannot create new tasks that are meaningfully different from previous ones, stop and return an empty task list.
            4. Bloom’s Taxonomy revised skill levels and explanations:
                1. Remembering: Recall or retrieve relevant knowledge from memory.
                2. Understanding: Construct meaning from instructional messages through summarizing, classifying, or explaining.
                3. Applying: Use learned knowledge to carry out procedures in new situations.
                4. Analyzing: Break material into parts and understand how these relate to each other and the overall structure.
                5. Evaluating: Make judgments based on criteria, standards, or evidence.
                6. Creating: Combine elements to generate new patterns or structures; reorganize knowledge to produce something original.
            5. Task Types: You can generate different types of tasks depending on the topic. The available types include:
                - MULTIPLE_CHOICE: A question with 3-4 possible answers, of which 1 is correct.
                - FREE_TEXT: An open-ended question that requires a detailed, text-based answer.
                - SHORT_ANSWER: A question that requires a short, direct response.
                - TRUE_FALSE: A simple true or false statement.
                - ESSAY: A more comprehensive question that requires an extended response in essay form.
                - CODING: A programming task (only if the topic is related to programming).
                - CODE_UNDERSTANDING: A task where students are given a code snippet and need to explain its functionality (only if the topic involves programming).
            6. Avoid duplicates: Ensure that each task is unique and evaluates a different aspect of the topic. If you are unable to create new, non-redundant tasks, stop and return with an empty task list.
                The output should be returned in the following **JSON format**:
                The output must be structured in a specific JSON format. **CorrectAnswer** must always be parseable as a `Map<String, String>`, even if it is simple text. Follow this structure strictly:
                Example JSON Output:
                {
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
                    "bloomLevel": "REMEMBERING",
                        "taskType": "CODING",
                        "question": "Write an if condition that checks if a variable is smaller than 5.",
                        "programmingLanguage": "java",
                        "correctAnswer": {
                            "text": "
                                     if (x < 5) {
                                       System.out.println("x is smaller than 5");
                                     }"
                        }
                 },
                {
                    "bloomLevel": "UNDERSTANDING",
                        "taskType": "CODE_UNDERSTANDING",
                        "question": "Explain Following code:
                            int x = 20;
                            int y = 18;
                            if (x > y) {
                              System.out.println("x is greater than y");
                            }",
                        "correctAnswer": {
                            "text": "This code defines 2 variables x and y. Then it checks if x is greater than y. If so it prints a message."
                        }
                 },
                      ]}                      ]
                    """)
    @UserMessage("""
            Generate tasks for the topic --{topic}-- of the course --{course}-- based on the Bloom levels of the learning goals. The keyword is --{keyword}--. Provide the response in the language --{language}--.
            Ensure tasks align with the given `Topic.maxBloom`, and output the results in the specified JSON format.
            """)
    String generate(@MemoryId String courseTopicId, String course, String topic, String keyword, String language);
}

