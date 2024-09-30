package de.htwg.learningpath.model;

public class TopicResult {
    private String readingMaterialJson;
    private String tasksJson;

    public TopicResult(String readingMaterialJson, String tasksJson) {
        this.readingMaterialJson = readingMaterialJson;
        this.tasksJson = tasksJson;
    }

    public String getReadingMaterialJson() {
        return readingMaterialJson;
    }

    public String getTasksJson() {
        return tasksJson;
    }
}
