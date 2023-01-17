package ru.yandex.practicum.ewm.request.exception;

public class RequestConfirmationNotRequiredException extends RuntimeException {

    public RequestConfirmationNotRequiredException(String message) {
        super(message);
    }
}
