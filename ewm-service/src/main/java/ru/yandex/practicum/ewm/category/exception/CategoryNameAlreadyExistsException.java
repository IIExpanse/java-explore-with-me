package ru.yandex.practicum.ewm.category.exception;

public class CategoryNameAlreadyExistsException extends RuntimeException {

    public CategoryNameAlreadyExistsException(String message) {
        super(message);
    }
}
