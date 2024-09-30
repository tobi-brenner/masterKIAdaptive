package de.htwg.course.strategy;

import de.htwg.course.model.TaskEvaluationResult;

public interface AnswerCheckStrategy {
    TaskEvaluationResult checkAnswer(String answer, String correctAnswer);
}
