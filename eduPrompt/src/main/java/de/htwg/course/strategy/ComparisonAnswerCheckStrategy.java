package de.htwg.course.strategy;

import de.htwg.course.model.TaskEvaluationResult;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ComparisonAnswerCheckStrategy implements AnswerCheckStrategy {
    /**
     * @param answer
     * @param correctAnswer
     * @return
     */
    @Override
    public TaskEvaluationResult checkAnswer(String answer, String correctAnswer) {
        boolean isCorrect = answer.equals(correctAnswer);
        String hint = isCorrect ? "" : "Check your answer and try again.";
        return new TaskEvaluationResult(isCorrect, answer, hint);
    }
}
