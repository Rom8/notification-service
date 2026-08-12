package ru.rom8.rescue.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.rom8.rescue.notification.avro.IncidentStatus;
import ru.rom8.rescue.notification.avro.NotificationEventV1;

@Slf4j
@Service
public class NotificationEventListener {

    @KafkaListener(topics = "volunteer_notification_event_v1")
    public void handle(NotificationEventV1 event) {
        IncidentStatus incidentStatus = event.getIncidentStatus();

        log.info("Сообщение отправлено");
    }
}
