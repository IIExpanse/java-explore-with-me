package ru.yandex.practicum.ewm.event.validator.impl;

import ru.yandex.practicum.ewm.event.validator.NullOrNotBlankConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NullOrNotBlankValidator implements ConstraintValidator<NullOrNotBlankConstraint, String> {
    @Override
    public void initialize(NullOrNotBlankConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || value.trim().length() > 0;
    }
}
