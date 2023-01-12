package ru.yandex.practicum.ewm.category.exception;

public class CantDeleteUsedCategoryException extends RuntimeException {

    public CantDeleteUsedCategoryException(String message) {
        super(message);
    }
}
