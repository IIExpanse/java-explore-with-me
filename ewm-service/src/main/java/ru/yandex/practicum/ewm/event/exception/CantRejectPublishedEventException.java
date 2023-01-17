package ru.yandex.practicum.ewm.event.exception;

public class CantRejectPublishedEventException extends RuntimeException {

    public CantRejectPublishedEventException(String message) {
        super(message);
    }
}
