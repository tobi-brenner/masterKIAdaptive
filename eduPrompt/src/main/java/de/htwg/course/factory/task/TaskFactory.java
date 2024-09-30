package de.htwg.course.factory.task;

import de.htwg.course.dto.GeneratedTasksResponseDTO;
import de.htwg.course.model.Task;

public interface TaskFactory {
    Task createTask(GeneratedTasksResponseDTO.TaskDTO taskDTO);
}
