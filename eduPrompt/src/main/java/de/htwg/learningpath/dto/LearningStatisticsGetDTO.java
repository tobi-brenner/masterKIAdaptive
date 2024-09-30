package de.htwg.learningpath.dto;

import de.htwg.course.model.BloomLevel;
import de.htwg.learningpath.model.LearningStatistics;

import java.time.LocalDateTime;

public class LearningStatisticsGetDTO {
    public Long id;
    public Long topicId;
    public BloomLevel currentBloomLevel;
    public long timeSpent;
    public String strengths;
    public String weaknesses;
    public String recommendations;
    public LocalDateTime recordedAt;

    public LearningStatisticsGetDTO(Long id, Long topicId, BloomLevel currentBloomLevel, long timeSpent,
                                    String strengths, String weaknesses, String recommendations) {
        this.id = id;
        this.topicId = topicId;
        this.currentBloomLevel = currentBloomLevel;
        this.timeSpent = timeSpent;
        this.strengths = strengths;
        this.weaknesses = weaknesses;
        this.recommendations = recommendations;
    }

    public LearningStatisticsGetDTO(LearningStatistics learningStatistics) {
        this.id = learningStatistics.id;
        this.topicId = learningStatistics.topic.id;
        this.currentBloomLevel = learningStatistics.currentBloomLevel;
        this.timeSpent = learningStatistics.timeSpent;
        this.strengths = learningStatistics.strengths;
        this.weaknesses = learningStatistics.weaknesses;
        this.recommendations = learningStatistics.recommendations;
        this.recordedAt = learningStatistics.recordedAt;
        
    }
}

