package de.htwg.course.factory.task;

import de.htwg.course.dto.GeneratedTasksResponseDTO;
import de.htwg.course.dto.initassessment.TaskDTO;
import de.htwg.course.model.BloomLevel;
import de.htwg.course.model.Task;
import de.htwg.course.model.tasktype.CodeCompletionTask;

public class CodeCompletionTaskFactory implements TaskFactory {
    @Override
    public Task createTask(GeneratedTasksResponseDTO.TaskDTO taskDTO) {
        CodeCompletionTask task = new CodeCompletionTask();

        task.question = taskDTO.question;
        task.correctAnswer = TaskUtils.convertCorrectAnswer(taskDTO.correctAnswer);
        task.bloomLevel = BloomLevel.valueOf(taskDTO.bloomLevel);
        return task;
    }
}
