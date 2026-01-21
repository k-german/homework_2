package org.hiber.kafka.producer;

import org.hiber.kafka.dto.UserNotificationEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserNotificationProducer {

    private static final String TOPIC = "user.notifications";

    private final KafkaTemplate<String, UserNotificationEvent> kafkaTemplate;

    public UserNotificationProducer(KafkaTemplate<String, UserNotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(UserNotificationEvent event) {
        kafkaTemplate.send(TOPIC, event.getEmail(), event);
    }
}
