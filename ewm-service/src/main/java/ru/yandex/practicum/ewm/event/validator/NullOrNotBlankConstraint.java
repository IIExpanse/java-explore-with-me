package ru.yandex.practicum.ewm.event.validator;

import ru.yandex.practicum.ewm.event.validator.impl.NullOrNotBlankValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NullOrNotBlankValidator.class)
public @interface NullOrNotBlankConstraint {

    String message() default "Обновляемое текстовое значение не может быть пустым.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
