package ru.yandex.practicum.ewm.event.validator;

import ru.yandex.practicum.ewm.event.validator.impl.EarliestStartTimeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EarliestStartTimeValidator.class)
public @interface EarliestStartTimeConstraint {

    String message() default "Дата и время начала проведения события не может быть раньше, " +
            "чем через два часа от текущего момента.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int[] days() default {};

    int[] hours() default {};

    int[] minutes() default {};
}
