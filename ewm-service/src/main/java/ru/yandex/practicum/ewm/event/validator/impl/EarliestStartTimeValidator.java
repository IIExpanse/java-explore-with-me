package ru.yandex.practicum.ewm.event.validator.impl;

import ru.yandex.practicum.ewm.event.validator.EarliestStartTimeConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class EarliestStartTimeValidator implements ConstraintValidator<EarliestStartTimeConstraint, LocalDateTime> {

    private static LocalDateTime edgeTime;

    @Override
    public void initialize(EarliestStartTimeConstraint constraintAnnotation) {
        edgeTime = LocalDateTime.now()
                .plusDays(constraintAnnotation.days()[0])
                .plusHours(constraintAnnotation.hours()[0])
                .plusMinutes(constraintAnnotation.minutes()[0]);

        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        return value == null || !value.isBefore(edgeTime);
    }
}
