package de.htwg.learningpath.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.htwg.course.model.BloomLevel;
import de.htwg.learningpath.model.LearningPath;
import de.htwg.learningpath.model.LearningStatistics;
import de.htwg.course.model.Topic;
import de.htwg.user.model.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EvaluationMapper {

    public List<LearningStatistics> mapEvaluationResponse(String evaluationResponse, User user, LearningPath learningPath, List<Topic> topics) {
        List<LearningStatistics> statisticsList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode rootNode = objectMapper.readTree(evaluationResponse);
            Iterator<String> fieldNames = rootNode.fieldNames();

            while (fieldNames.hasNext()) {
                String topicName = fieldNames.next();
                JsonNode topicNode = rootNode.get(topicName);

                LearningStatistics statistics = new LearningStatistics();
                statistics.user = user;
                statistics.learningPath = learningPath;
                statistics.topic = topics.stream()
                        .filter(topic -> topic.name.equals(topicName))
                        .findFirst()
                        .orElse(null);

                statistics.currentBloomLevel = BloomLevel.valueOf(topicNode.path("levelOfKnowledge").asText().toUpperCase());
                statistics.strengths = topicNode.path("strengths").asText("");
                statistics.weaknesses = topicNode.path("weaknesses").asText("");
                statistics.recommendations = topicNode.path("recommendations").asText("");

                statisticsList.add(statistics);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return statisticsList;
    }


}

