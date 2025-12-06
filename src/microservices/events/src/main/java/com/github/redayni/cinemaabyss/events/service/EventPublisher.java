package com.github.redayni.cinemaabyss.events.service;

import com.github.redayni.cinemaabyss.events.config.TopicsProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class EventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final TopicsProperties topics;

    public EventPublisher(
        KafkaTemplate<String, String> kafkaTemplate,
        ObjectMapper objectMapper,
        TopicsProperties topics
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topics = topics;
    }

    public SendResult<String, String> publishMovieEvent(String eventId, Object payload) throws Exception {
        return publishInternal(topics.movie(), eventId, EventType.MOVIE, payload);
    }

    public SendResult<String, String> publishUserEvent(String eventId, Object payload) throws Exception {
        return publishInternal(topics.user(), eventId, EventType.USER, payload);
    }

    public SendResult<String, String> publishPaymentEvent(String eventId, Object payload) throws Exception {
        return publishInternal(topics.payment(), eventId, EventType.PAYMENT, payload);
    }

    private SendResult<String, String> publishInternal(
        String topic,
        String eventId,
        EventType type,
        Object payload
    ) throws Exception {
        JsonNode payloadNode = objectMapper.valueToTree(payload);

        String value = objectMapper.writeValueAsString(Map.of(
            "id", eventId,
            "type", type.getName(),
            "timestamp", Instant.now(),
            "payload", payloadNode
        ));

        return kafkaTemplate.send(topic, eventId, value).get(3, TimeUnit.SECONDS);
    }
}
