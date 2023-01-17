package ru.yandex.practicum.ewm.request.exception;

public class RequestAlreadyRejectedException extends RuntimeException {

    public RequestAlreadyRejectedException(String message) {
        super(message);
    }
}
