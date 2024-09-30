package de.htwg.course.strategy;

import de.htwg.course.model.TaskEvaluationResult;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LLMAnswerCheckStrategy implements AnswerCheckStrategy {
    /**
     * @param answer
     * @param correctAnswer
     * @return
     */
    @Override
    public TaskEvaluationResult checkAnswer(String answer, String correctAnswer) {

        return null;
    }

    private boolean getLLMEvaluation(String answer, String correctAnswer) {
        // LLM evaluation logic here
        return true; // Placeholder
    }
}
