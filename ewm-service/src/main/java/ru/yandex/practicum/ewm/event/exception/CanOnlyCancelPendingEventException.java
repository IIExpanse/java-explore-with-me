package ru.yandex.practicum.ewm.event.exception;

public class CanOnlyCancelPendingEventException extends RuntimeException {

    public CanOnlyCancelPendingEventException(String message) {
        super(message);
    }
}
