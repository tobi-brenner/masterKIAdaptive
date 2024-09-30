package de.htwg.learningpath.dto;


import de.htwg.course.dto.courses.get.TopicGetDTO;
import de.htwg.course.model.Task;
import de.htwg.learningpath.model.UserTaskAttempt;
import de.htwg.user.model.User;

import java.time.LocalDateTime;


public class UserTaskAttemptDTO {
    public Long id;
    public UserDTO user;
    public TaskDTO task;
    public String answer;
    public String feedback;
    public LocalDateTime attemptTime;
    public boolean isCorrect;
    public Long assessmentId;
    public Long learningStepId;
    public Long learningPathId;


    public UserTaskAttemptDTO(UserTaskAttempt attempt) {
        this.id = attempt.id;
        this.user = new UserDTO(attempt.user);
        this.task = new TaskDTO(attempt.task);
        this.answer = attempt.answer != null ? attempt.answer.answer : null;
        this.feedback = attempt.answer != null ? attempt.answer.llmResponse : null;
        this.isCorrect = attempt.isCorrect;
        this.assessmentId = attempt.assessment != null ? attempt.assessment.id : null;
        this.learningStepId = attempt.learningStep != null ? attempt.learningStep.id : null;
        this.learningPathId = attempt.learningPath != null ? attempt.learningPath.id : null;
    }

    public static class UserDTO {
        public Long id;
        public String firstName;
        public String lastName;
        public String email;

        public UserDTO(User user) {
            this.id = user.id;
            this.firstName = user.firstName;
            this.lastName = user.lastName;
            this.email = user.email;
        }
    }

    public static class TaskDTO {
        public Long id;
        public String question;
        public String taskType;
        public TopicGetDTO topic;

        public TaskDTO(Task task) {
            this.id = task.id;
            this.question = task.question;
            this.taskType = task.taskType;
            this.topic = new TopicGetDTO(task.topic);
        }
    }
}


