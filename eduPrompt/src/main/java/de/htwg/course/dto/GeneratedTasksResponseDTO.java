package de.htwg.course.dto;

import java.util.List;
import java.util.Map;

public class GeneratedTasksResponseDTO {
    public List<TaskDTO> tasks;

    public static class TaskDTO {
        public String bloomLevel;
        public String taskType;
        public String question;
        public Map<String, String> options;
        public Map<String, String> correctAnswer;
        public String programmingLanguage;
    }
}
