package de.htwg.course.factory.task;

import de.htwg.course.dto.GeneratedTasksResponseDTO;
import de.htwg.course.model.BloomLevel;
import de.htwg.course.model.Task;
import de.htwg.course.model.tasktype.CodeUnderstandingTask;

public class CodeUnderstandingTaskFactory implements TaskFactory {


    /**
     * @param taskDTO
     * @return
     */
    @Override
    public Task createTask(GeneratedTasksResponseDTO.TaskDTO taskDTO) {
        CodeUnderstandingTask task = new CodeUnderstandingTask();
        task.question = taskDTO.question;
        task.correctAnswer = TaskUtils.convertCorrectAnswer(taskDTO.correctAnswer);
        task.bloomLevel = BloomLevel.valueOf(taskDTO.bloomLevel);
        return task;
    }
}
