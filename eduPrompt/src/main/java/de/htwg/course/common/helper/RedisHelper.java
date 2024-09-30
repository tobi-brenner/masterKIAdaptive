package de.htwg.course.common.helper;

import io.quarkus.redis.client.RedisClient;
import io.quarkus.redis.client.reactive.ReactiveRedisClient;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public class RedisHelper {

    @Inject
    RedisClient redisClient;

    @Inject
    ReactiveRedisClient reactiveRedisClient;

    public void saveDocument(String key, String documentText) {
        redisClient.set(List.of(key, documentText));
    }

    public String getDocument(String key) {
        return redisClient.get(key).toString();
    }
}