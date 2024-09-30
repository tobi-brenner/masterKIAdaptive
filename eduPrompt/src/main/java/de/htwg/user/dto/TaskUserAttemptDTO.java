package de.htwg.user.dto;

import de.htwg.course.model.Task;
import de.htwg.learningpath.model.UserTaskAttempt;


public class TaskUserAttemptDTO {
    public Long taskId;
    public String question;
    public String taskType;
    public UserTaskAttemptDTO userTaskAttempt;

    public TaskUserAttemptDTO(Task task, UserTaskAttempt userTaskAttempt) {
        this.taskId = task.id;
        this.question = task.question;
        this.taskType = task.taskType;
        this.userTaskAttempt = new UserTaskAttemptDTO(userTaskAttempt);
    }
}

