package de.htwg.course.factory.task;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class TaskUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String convertCorrectAnswer(Object correctAnswer) {
        if (correctAnswer instanceof String) {
            return (String) correctAnswer;
        } else if (correctAnswer instanceof Map) {
            try {
                // Convert the Map to a properly formatted JSON string
                return objectMapper.writeValueAsString(correctAnswer);
            } catch (Exception e) {
                throw new RuntimeException("Failed to convert correctAnswer to JSON", e);
            }
        } else {
            throw new IllegalArgumentException("Unsupported correctAnswer type: " + correctAnswer.getClass().getName());
        }
    }
}
