package ru.practicum.shareit.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class StartBeforeEndValidatorTest {

    private final StartBeforeEndValidator validator = new StartBeforeEndValidator();
    private final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

    @Test
    void isValid_ShouldReturnTrueWhenStartBeforeEnd() {
        BookItemRequestDto dto = new BookItemRequestDto(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );

        boolean result = validator.isValid(dto, context);
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @MethodSource("invalidDateCombinations")
    void isValid_ShouldReturnFalseForInvalidCases(LocalDateTime start, LocalDateTime end) {
        BookItemRequestDto dto = new BookItemRequestDto(1L, start, end);

        boolean result = validator.isValid(dto, context);
        assertThat(result).isFalse();
    }

    private static Stream<Arguments> invalidDateCombinations() {
        LocalDateTime now = LocalDateTime.now();
        return Stream.of(
                Arguments.of(now.plusHours(2), now.plusHours(1)),  // start > end
                Arguments.of(now, now),                           // start == end
                Arguments.of(now.minusDays(1), now.minusDays(2))  // both in past but start > end
        );
    }

    @Test
    void isValid_ShouldHandleEdgeCases() {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
        LocalDateTime end = start.plusNanos(1); // на 1 наносекунду позже

        BookItemRequestDto dto = new BookItemRequestDto(1L, start, end);
        assertThat(validator.isValid(dto, context)).isTrue();
    }
}