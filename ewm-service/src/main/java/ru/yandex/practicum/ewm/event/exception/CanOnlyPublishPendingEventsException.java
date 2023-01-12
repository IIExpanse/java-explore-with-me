package ru.yandex.practicum.ewm.event.exception;

public class CanOnlyPublishPendingEventsException extends RuntimeException {

    public CanOnlyPublishPendingEventsException(String message) {
        super(message);
    }
}
