package ru.yandex.practicum.ewm.event.exception;

public class CantEditPublishedEventException extends RuntimeException {

    public CantEditPublishedEventException(String message) {
        super(message);
    }
}
