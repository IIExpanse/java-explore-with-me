package ru.yandex.practicum.ewm.request.exception;

public class RequestAlreadyApprovedException extends RuntimeException {

    public RequestAlreadyApprovedException(String message) {
        super(message);
    }
}
