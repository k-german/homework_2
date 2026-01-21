package org.hiber.kafka.producer;

import org.hiber.kafka.dto.UserNotificationEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class TestUserNotificationProducer extends UserNotificationProducer {

    public TestUserNotificationProducer() {
        super(null);
    }

    @Override
    public void send(UserNotificationEvent event) {
        // test
    }
}
