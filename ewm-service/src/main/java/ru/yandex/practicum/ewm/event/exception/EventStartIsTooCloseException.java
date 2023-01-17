package ru.yandex.practicum.ewm.event.exception;

public class EventStartIsTooCloseException extends RuntimeException {

    public EventStartIsTooCloseException(String message) {
        super(message);
    }
}
