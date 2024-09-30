package de.htwg.course.dto.initassessment;

import java.util.List;

public class SelectedTasksDTO {
    public List<TopicTasksDTO> topics;

    public static class TopicTasksDTO {
        public Long topicId;
        public List<Long> selectedTaskIds;
    }
}

