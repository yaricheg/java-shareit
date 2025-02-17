package ru.practicum.shareit.booking.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = FutureDateTimeValidator.class)
public @interface FutureDateTime {

    String message() default "Указанная дата и время должны быть в будущем.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String value();
}

