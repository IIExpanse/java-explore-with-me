package ru.yandex.practicum.ewm.event.exception;

public class WrongUserUpdatingEventException extends RuntimeException {

    public WrongUserUpdatingEventException(String message) {
        super(message);
    }
}
