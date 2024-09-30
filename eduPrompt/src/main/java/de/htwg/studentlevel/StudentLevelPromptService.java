package de.htwg.studentlevel;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RegisterAiService
public interface StudentLevelPromptService {
    @SystemMessage("""
            You are an assistant in evaluating a students personal learning level.
            You are evaluating the level of the students knowledge in the blooms taxonomy, responding with a JSON document.
            The blooms evaluation values are one of the following:
                *REMEMBERING,
                *UNDERSTANDING,
                *APPLYING,
                *ANALYZING,
                *EVALUATING,
                *CREATING,
            You will be given a topic on which the student should be evaluated.
            Related to the topic there will be exercises with their corresponding students answers given to you.
            Analyze these exercise answers to identify strengths and weaknesses of the student.
            There should be relevant materials embedded. Use provided embeddings if applicable.
            Return a JSON with the arising Blooms Evaluation and message with the weaknesses and stenghts.
            For example:
                        
                evaluation: ANALYZING,
                message: "The answers with regard to Amdahl's Law were not yet very good.
                 You might want to look into it more
                  - Speedup and Amdahl’s Law: The potential speedup ( S ) from using P processors is limited by the serial portion of the program.
                   Amdahl’s Law gives a formula to calculate the theoretical maximum speedup.
                  - Theoretical limits on speedup based on the proportion of serial execution in a program
                  - Utilization rates of cores under different conditions
                 "
                 Also state if you have used RAG of the Redis DB for your answer at the end.
                       
            """
    )
    @UserMessage("""
                Your task is to evaluate the test and and classify the students level. ---.
                Evaluate the tasks for the classification.
                Also depending on successful or failed tasks recommend topics to study more in detail.
                The topic, questions and the answers of the Student are provided in following JSON data.
                ---
                {prompt}
                ---
                
            """)
    String determineLevel(@MemoryId String memoryId, String prompt);
}


//@UserMessage("""
//                Your task is to evaluate the test and and classify the students level. ---.
//                Evaluate the tasks for the classification.
//                Also depending on successful or failed tasks recommend topics to study more in detail.
//                The topic of the evaluation is {topicName}.
//                The questions related to it are represented with the following JSON-Format data:
//                    ---
//                    {questions}
//                    ---
//
//                The Students answers to the questions are represented with the following JSON data:
//                ---
//                {answers}
//                ---
//            """)

//For example:
//        - `I love your bank, you are the best!` is a 'POSITIVE' review
//        - `J'adore votre banque` is a 'POSITIVE' review
//        - `I hate your bank, you are the worst!` is a 'NEGATIVE' review
//
//Respond with a JSON document containing:
//        - the 'evaluation' key set to 'POSITIVE' if the review is
//positive, 'NEGATIVE' otherwise
//        - the 'message' key set to a message thanking or apologizing
//to the customer. These messages must be polite and match the
//review's language.
