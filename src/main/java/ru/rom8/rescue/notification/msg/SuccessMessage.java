package ru.rom8.rescue.notification.msg;

import ru.rom8.rescue.notification.avro.IncidentStatus;

/**
 * Сообщение для инцидента в статусе {@link IncidentStatus#SUCCESS}
 */
public class SuccessMessage implements Message {

    @Override
    public String title() {
        return "";
    }

    @Override
    public String body() {
        return "";
    }
}
