package de.htwg.user.dto;

import de.htwg.learningpath.model.UserTaskAttempt;

import java.time.LocalDateTime;
//public class UserTaskAttemptDTO {
//    public Long id;
//    public String taskName; // Add other fields you need
//
//    // Constructors, getters and setters
//    public UserTaskAttemptDTO() {
//    }
//
//    public UserTaskAttemptDTO(Long id, String taskName) {
//        this.id = id;
//        this.taskName = taskName;
//    }
//}


public class UserTaskAttemptDTO {
    public Long id;
    public boolean isCorrect;
    public LocalDateTime attemptTime;
    public TaskAnswerDTO answer;

    public UserTaskAttemptDTO(UserTaskAttempt userTaskAttempt) {
        this.id = userTaskAttempt.id;
        this.isCorrect = userTaskAttempt.isCorrect;
        this.attemptTime = userTaskAttempt.attemptTime;
        this.answer = userTaskAttempt.answer != null ? new TaskAnswerDTO(userTaskAttempt.answer) : null;
    }
}

