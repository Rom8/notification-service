package ru.rom8.rescue.notification.msg;

public interface Message {

    /**
     * Заголовок
     */
    String title();

    /**
     * Тело сообщения
     */
    String body();
}
