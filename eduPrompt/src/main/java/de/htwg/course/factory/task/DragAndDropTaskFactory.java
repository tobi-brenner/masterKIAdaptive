package de.htwg.course.factory.task;


import de.htwg.course.dto.GeneratedTasksResponseDTO;
import de.htwg.course.dto.initassessment.TaskDTO;
import de.htwg.course.model.BloomLevel;
import de.htwg.course.model.Task;
import de.htwg.course.model.tasktype.DragAndDropTask;

public class DragAndDropTaskFactory implements TaskFactory {
    @Override
    public Task createTask(GeneratedTasksResponseDTO.TaskDTO taskDTO) {
        DragAndDropTask task = new DragAndDropTask();
        task.setOptions(taskDTO.options);

        task.question = taskDTO.question;
        task.correctAnswer = TaskUtils.convertCorrectAnswer(taskDTO.correctAnswer);
        task.bloomLevel = BloomLevel.valueOf(taskDTO.bloomLevel);
        return task;
    }
}

