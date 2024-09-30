package de.htwg.user.dto;

import de.htwg.course.model.Task;
import de.htwg.learningpath.model.UserTaskAttempt;

import java.util.List;

public class TaskUserAttemptsCountDTO {
    public Task task;
    public List<UserTaskAttempt> userTaskAttempts;
    public long count;

    public TaskUserAttemptsCountDTO(Task task, List<UserTaskAttempt> userTaskAttempts, long count) {
        this.task = task;
        this.userTaskAttempts = userTaskAttempts;
        this.count = count;
    }
}

