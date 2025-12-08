package com.github.redayni.cinemaabyss.events.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventListener {
    private static final Logger log = LoggerFactory.getLogger(EventListener.class);

    @KafkaListener(
        topics = {"${app.kafka.topics.movie}", "${app.kafka.topics.user}", "${app.kafka.topics.payment}"},
        groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onMessage(ConsumerRecord<String, String> record) {
        log.info(
            "Consumed event: topic={}, partition={}, offset={}, key={}, value={}",
            record.topic(), record.partition(), record.offset(), record.key(), record.value()
        );
    }
}
