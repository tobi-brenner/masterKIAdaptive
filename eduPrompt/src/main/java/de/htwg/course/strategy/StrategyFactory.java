package de.htwg.course.strategy;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class StrategyFactory {
    @Inject
    ComparisonAnswerCheckStrategy comparisonStrategy;

    @Inject
    LLMAnswerCheckStrategy llmStrategy;

    public AnswerCheckStrategy getStrategy(String taskType) {
        switch (taskType) {
            case "FREE_TEXT":
                return llmStrategy;
            case "MULTIPLE_CHOICE":
            default:
                return comparisonStrategy;
        }
    }
}
