package ru.yandex.practicum.ewm.request.exception;

public class CantParticipateInUnpublishedEventException extends RuntimeException {

    public CantParticipateInUnpublishedEventException(String message) {
        super(message);
    }
}
