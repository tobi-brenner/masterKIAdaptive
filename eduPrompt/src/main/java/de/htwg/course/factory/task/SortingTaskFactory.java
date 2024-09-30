package de.htwg.course.factory.task;


import de.htwg.course.dto.GeneratedTasksResponseDTO;
import de.htwg.course.dto.initassessment.TaskDTO;
import de.htwg.course.model.BloomLevel;
import de.htwg.course.model.Task;
import de.htwg.course.model.tasktype.ShortAnswerTask;
import de.htwg.course.model.tasktype.SortingTask;

public class SortingTaskFactory implements TaskFactory {


    @Override
    public Task createTask(GeneratedTasksResponseDTO.TaskDTO taskDTO) {
        SortingTask task = new SortingTask();
        task.question = taskDTO.question;
        task.setOptions(taskDTO.options);
        task.correctAnswer = TaskUtils.convertCorrectAnswer(taskDTO.correctAnswer);
        task.bloomLevel = BloomLevel.valueOf(taskDTO.bloomLevel);
        return task;
    }
}
