package ru.yandex.practicum.ewm.event.exception;

public class WrongUserRequestingOwnerEventException extends RuntimeException {

    public WrongUserRequestingOwnerEventException(String message) {
        super(message);
    }
}
