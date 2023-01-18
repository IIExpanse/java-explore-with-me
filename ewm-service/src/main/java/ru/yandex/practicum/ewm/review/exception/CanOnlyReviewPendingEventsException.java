package ru.yandex.practicum.ewm.review.exception;

public class CanOnlyReviewPendingEventsException extends RuntimeException {

    public CanOnlyReviewPendingEventsException(String message) {
        super(message);
    }
}
