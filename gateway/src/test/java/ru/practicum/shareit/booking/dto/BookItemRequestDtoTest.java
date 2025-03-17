package ru.practicum.shareit.booking.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class BookItemRequestDtoTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void noArgsConstructor_CreatesEmptyObject() {
        BookItemRequestDto dto = new BookItemRequestDto();

        assertAll(
                () -> assertEquals(0L, dto.getItemId()),
                () -> assertNull(dto.getStart()),
                () -> assertNull(dto.getEnd())
        );
    }

    @Test
    void allArgsConstructor_PopulatesAllFields() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookItemRequestDto dto = new BookItemRequestDto(1L, start, end);

        assertAll(
                () -> assertEquals(1L, dto.getItemId()),
                () -> assertEquals(start, dto.getStart()),
                () -> assertEquals(end, dto.getEnd())
        );
    }

    @Test
    void validation_SuccessForValidDates() {
        BookItemRequestDto dto = new BookItemRequestDto(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusDays(1)
        );

        var violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Не должно быть нарушений валидации");
    }

    @Test
    void validation_FailsWhenStartInPast() {
        BookItemRequestDto dto = new BookItemRequestDto(
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1)
        );

        var violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Дата начала бронирования не должна быть в прошлом", violations.iterator().next().getMessage());
    }

    @Test
    void validation_FailsWhenEndInPast() {
        BookItemRequestDto dto = new BookItemRequestDto(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().minusDays(1)
        );

        var violations = validator.validate(dto);

        assertAll(
                () -> assertEquals(2, violations.size()),
                () -> assertTrue(violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.toSet())
                        .containsAll(Set.of(
                                "Дата окончания бронирования должна быть больше текущей",
                                "Дата окончания должна быть позже даты начала"
                        )))
        );;
    }

    @Test
    void validation_FailsWhenStartEqualsEnd() {
        LocalDateTime time = LocalDateTime.now().plusDays(1);
        BookItemRequestDto dto = new BookItemRequestDto(1L, time, time);

        var violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Дата окончания должна быть позже даты начала", violations.iterator().next().getMessage());
    }
}