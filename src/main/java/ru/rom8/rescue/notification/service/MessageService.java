package ru.rom8.rescue.notification.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ru.rom8.rescue.notification.avro.IncidentStatus;
import ru.rom8.rescue.notification.avro.NotificationEventParamV1;
import ru.rom8.rescue.notification.avro.NotificationEventV1;
import ru.rom8.rescue.notification.avro.ParamType;
import ru.rom8.rescue.notification.msg.Message;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final String inProgressTitleTemplate;
    private final String inProgressBodyTemplate;
    private final String successTitleTemplate;
    private final String successBodyTemplate;
    private final String failTitleTemplate;
    private final String failBodyTemplate;

    public MessageService(
            @Value("classpath:messages/in-progress/title.txt") Resource inProgressTitle,
            @Value("classpath:messages/in-progress/body.txt") Resource inProgressBody,
            @Value("classpath:messages/success/title.txt") Resource successTitle,
            @Value("classpath:messages/success/body.txt") Resource successBody,
            @Value("classpath:messages/fail/title.txt") Resource failTitle,
            @Value("classpath:messages/fail/body.txt") Resource failBody) {
        this.inProgressTitleTemplate = readTemplate(inProgressTitle);
        this.inProgressBodyTemplate = readTemplate(inProgressBody);
        this.successTitleTemplate = readTemplate(successTitle);
        this.successBodyTemplate = readTemplate(successBody);
        this.failTitleTemplate = readTemplate(failTitle);
        this.failBodyTemplate = readTemplate(failBody);
    }

    Message createMessage(NotificationEventV1 event) {
        IncidentStatus incidentStatus = event.getIncidentStatus();
        Map<ParamType, String> params = event.getParams().stream()
                .collect(Collectors.toMap(
                        NotificationEventParamV1::getParamType,
                        NotificationEventParamV1::getParamValue,
                        (val1, val2) -> val1));

        return switch (incidentStatus) {
            case IN_PROGRESS -> new Message(
                    render(inProgressTitleTemplate, params),
                    render(inProgressBodyTemplate, params));
            case SUCCESS -> new Message(
                    render(successTitleTemplate, params),
                    render(successBodyTemplate, params));
            case FAIL -> new Message(
                    render(failTitleTemplate, params),
                    render(failBodyTemplate, params));
        };
    }

    private static String render(String template, Map<ParamType, String> params) {
        String result = template;
        for (Map.Entry<ParamType, String> entry : params.entrySet()) {
            result = result.replace(
                    "%{" + entry.getKey().name() + "}",
                    entry.getValue());
        }
        return result;
    }

    private static String readTemplate(Resource resource) {
        try {
            return resource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Не удалось прочитать шаблон сообщения: " + resource, exception);
        }
    }
}
