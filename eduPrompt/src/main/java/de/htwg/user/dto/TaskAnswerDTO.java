package de.htwg.user.dto;

import de.htwg.learningpath.model.TaskAnswer;

public class TaskAnswerDTO {
    public String answer;
    public String feedback;

    public TaskAnswerDTO(TaskAnswer taskAnswer) {
        this.answer = taskAnswer.answer;
        this.feedback = taskAnswer.llmResponse;
    }
}

