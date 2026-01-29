package org.hiber.kafka.producer;

import org.hiber.kafka.dto.UserNotificationEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component
@Profile("!test")
public class UserNotificationProducer {

//    private static final String TOPIC = "user.notifications";

    private final KafkaTemplate<String, UserNotificationEvent> kafkaTemplate;

    @Value("${kafka.topic.user-notifications}")
    private String topic;

    public UserNotificationProducer(KafkaTemplate<String, UserNotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(UserNotificationEvent event) {
        kafkaTemplate.send(topic, event.getEmail(), event);
    }
}
