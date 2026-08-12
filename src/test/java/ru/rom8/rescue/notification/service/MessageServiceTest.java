package ru.rom8.rescue.notification.service;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import ru.rom8.rescue.notification.avro.IncidentStatus;
import ru.rom8.rescue.notification.avro.NotificationEventParamV1;
import ru.rom8.rescue.notification.avro.NotificationEventV1;
import ru.rom8.rescue.notification.avro.ParamType;
import ru.rom8.rescue.notification.msg.Message;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MessageServiceTest {

    MessageService messageService = new MessageService(
            new ClassPathResource("messages/in-progress/title.txt"),
            new ClassPathResource("messages/in-progress/body.txt"),
            new ClassPathResource("messages/success/title.txt"),
            new ClassPathResource("messages/success/body.txt"),
            new ClassPathResource("messages/fail/title.txt"),
            new ClassPathResource("messages/fail/body.txt"));

    @Test
    @DisplayName("Формирует сообщение о продолжающемся поиске со всеми подставленными параметрами")
    void createsInProgressMessageWithAllBodyParametersReplaced() throws IOException {
        NotificationEventV1 event = new NotificationEventV1(
                UUID.randomUUID(),
                Instant.now(),
                "test-producer",
                UUID.randomUUID(),
                IncidentStatus.IN_PROGRESS,
                List.of(
                        new NotificationEventParamV1(ParamType.DESCRIPTION, "красная куртка"),
                        new NotificationEventParamV1(ParamType.LAT, "55.7558"),
                        new NotificationEventParamV1(ParamType.LON, "37.6176"),
                        new NotificationEventParamV1(ParamType.LOCATION, "центральный район"),
                        new NotificationEventParamV1(ParamType.PERSON_NAME, "Иванов Иван"),
                        new NotificationEventParamV1(ParamType.PHOTO_URL, "https://example.test/photo"),
                        new NotificationEventParamV1(ParamType.PERSON_AGE, "35"),
                        new NotificationEventParamV1(ParamType.IDENTIFYING_MARKS, "шрам на подбородке"),
                        new NotificationEventParamV1(ParamType.LAST_SEEN_CLOTHES, "синие джинсы"),
                        new NotificationEventParamV1(ParamType.LAST_SEEN_DATE, "вчера")),
                List.of());

        Message message = messageService.createMessage(event);

        assertThat(message.title()).isEqualTo(readFromFile("messages/in-progress/title_expected.txt"));
        assertThat(message.body()).isEqualTo(readFromFile("messages/in-progress/body_expected.txt"));
    }

    @Test
    @DisplayName("Формирует сообщение об успешном завершении поиска")
    void createsSuccessMessageWithExpectedTitleAndBody() throws IOException {
        NotificationEventV1 event = new NotificationEventV1(
                UUID.randomUUID(),
                Instant.now(),
                "test-producer",
                UUID.randomUUID(),
                IncidentStatus.SUCCESS,
                List.of(new NotificationEventParamV1(ParamType.PERSON_NAME, "Иванов Иван")),
                List.of());

        Message message = messageService.createMessage(event);

        assertThat(message.title()).isEqualTo(readFromFile("messages/success/title_expected.txt"));
        assertThat(message.body()).isEqualTo(readFromFile("messages/success/body_expected.txt"));
    }

    @Test
    @DisplayName("Формирует сообщение о неуспешном завершении поиска")
    void createsFailMessageWithExpectedTitleAndBody() throws IOException {
        NotificationEventV1 event = new NotificationEventV1(
                UUID.randomUUID(),
                Instant.now(),
                "test-producer",
                UUID.randomUUID(),
                IncidentStatus.FAIL,
                List.of(new NotificationEventParamV1(ParamType.PERSON_NAME, "Иванов Иван")),
                List.of());

        Message message = messageService.createMessage(event);

        assertThat(message.title()).isEqualTo(readFromFile("messages/fail/title_expected.txt"));
        assertThat(message.body()).isEqualTo(readFromFile("messages/fail/body_expected.txt"));
    }

    private static @NonNull String readFromFile(String file) throws IOException {
        return new ClassPathResource(file).getContentAsString(StandardCharsets.UTF_8);
    }
}
