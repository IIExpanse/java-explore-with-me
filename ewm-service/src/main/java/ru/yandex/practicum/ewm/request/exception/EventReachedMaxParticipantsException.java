package ru.yandex.practicum.ewm.request.exception;

public class EventReachedMaxParticipantsException extends RuntimeException {

    public EventReachedMaxParticipantsException(String message) {
        super(message);
    }
}
