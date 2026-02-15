package com.SIGMA.USCO.notifications.publisher;

import com.SIGMA.USCO.notifications.event.DomainEvent;
import com.SIGMA.USCO.notifications.event.SeminarCancelledEvent;
import com.SIGMA.USCO.notifications.event.SeminarStartedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventPublisher {
    private final ApplicationEventPublisher publisher;

    public void publish(DomainEvent event) {
        publisher.publishEvent(event);
    }

    public void publishSeminarStartedEvent(SeminarStartedEvent event) {
        publisher.publishEvent(event);
    }

    public void publishSeminarCancelledEvent(SeminarCancelledEvent event) {
        publisher.publishEvent(event);
    }
}
