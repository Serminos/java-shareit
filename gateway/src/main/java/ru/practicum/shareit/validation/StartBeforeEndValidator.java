package ru.practicum.shareit.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, BookItemRequestDto> {
    @Override
    public boolean isValid(BookItemRequestDto dto, ConstraintValidatorContext context) {
        return dto.getStart().isBefore(dto.getEnd());
    }
}
