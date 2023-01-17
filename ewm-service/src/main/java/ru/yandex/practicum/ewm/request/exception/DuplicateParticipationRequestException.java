package ru.yandex.practicum.ewm.request.exception;

public class DuplicateParticipationRequestException extends RuntimeException {

    public DuplicateParticipationRequestException(String message) {
        super(message);
    }
}
