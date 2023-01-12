package ru.yandex.practicum.ewm.request.exception;

public class WrongUserQueryingEventRequestsException extends RuntimeException {

    public WrongUserQueryingEventRequestsException(String message) {
        super(message);
    }
}
