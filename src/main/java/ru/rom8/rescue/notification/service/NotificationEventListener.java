package ru.rom8.rescue.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.rom8.rescue.notification.avro.NotificationEventV1;
import ru.rom8.rescue.notification.msg.Message;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationEventListener {

    private final MessageService messageService;

    @KafkaListener(topics = "volunteer_notification_event_v1")
    public void handle(NotificationEventV1 event) {
        Message message = messageService.createMessage(event);

        log.atInfo()
                .addKeyValue("Заголовок", message.title())
                .addKeyValue("Тело сообщения", message.body())
                .log("Новое сообщение");
    }
}
