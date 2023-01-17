package ru.yandex.practicum.ewm.request.exception;

public class WrongUserUpdatingRequestException extends RuntimeException {

    public WrongUserUpdatingRequestException(String message) {
        super(message);
    }
}
