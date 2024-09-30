package de.htwg.course.factory.task;

import de.htwg.course.dto.GeneratedTasksResponseDTO;
import de.htwg.course.dto.initassessment.TaskDTO;
import de.htwg.course.model.BloomLevel;
import de.htwg.course.model.Task;
import de.htwg.course.model.tasktype.CodingTask;

public class CodingTaskFactory implements TaskFactory {
    @Override
    public Task createTask(GeneratedTasksResponseDTO.TaskDTO taskDTO) {
        CodingTask task = new CodingTask();
        task.setProgrammingLanguage(taskDTO.programmingLanguage);
        task.question = taskDTO.question;
        task.correctAnswer = TaskUtils.convertCorrectAnswer(taskDTO.correctAnswer);
        task.bloomLevel = BloomLevel.valueOf(taskDTO.bloomLevel);
        return task;
    }
}
