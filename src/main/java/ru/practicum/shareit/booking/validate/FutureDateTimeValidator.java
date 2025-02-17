package ru.practicum.shareit.booking.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.exception.BadRequestException;

import java.time.LocalDateTime;

public class FutureDateTimeValidator implements ConstraintValidator<FutureDateTime, LocalDateTime> {

    private String dateTimeString;

    @Override
    public void initialize(FutureDateTime constraintAnnotation) {
        this.dateTimeString = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (value.isAfter(LocalDateTime.now())) {
            return value.isAfter(LocalDateTime.now());
        }
        throw new BadRequestException("Время бронирования должно быть позже текущего времени");
    }
}
