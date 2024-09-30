package de.htwg.course.factory.task;


import de.htwg.course.dto.GeneratedTasksResponseDTO;
import de.htwg.course.dto.TaskDTO;

public class TaskFactoryProducer {

    public static TaskFactory getFactory(GeneratedTasksResponseDTO.TaskDTO taskDTO) {
        switch (taskDTO.taskType.toUpperCase()) {
            case "MULTIPLE_CHOICE":
                return new MultipleChoiceTaskFactory();
            case "MATCHING":
                return new MatchingTaskFactory();
            case "SORTING":
                return new SortingTaskFactory();
            case "DRAG_AND_DROP":
                return new DragAndDropTaskFactory();
            case "FREE_TEXT":
                return new FreeTextTaskFactory();
            case "SHORT_ANSWER":
                return new ShortAnswerTaskFactory();
            case "TRUE_FALSE":
                return new TrueFalseTaskFactory();
            case "CODE_COMPLETION":
                return new CodeCompletionTaskFactory();
            case "ESSAY":
                return new EssayTaskFactory();
            case "CODING":
                return new CodingTaskFactory();
            case "CODE_UNDERSTANDING":
                return new CodeUnderstandingTaskFactory();
            default:
                throw new IllegalArgumentException("Unsupported task type: " + taskDTO.taskType);
        }
    }
}
