package ru.yandex.practicum.ewm.review.exception;

public class WrongUserRequestingEventReviewsException extends RuntimeException {

    public WrongUserRequestingEventReviewsException(String message) {
        super(message);
    }
}
