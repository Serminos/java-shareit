package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingStateTest {
    @ParameterizedTest
    @EnumSource(BookingState.class)
    void from_ShouldReturnStateForValidStrings(BookingState state) {
        assertAll(
                () -> assertEquals(Optional.of(state), BookingState.from(state.name().toUpperCase())),
                () -> assertEquals(Optional.of(state), BookingState.from(state.name().toLowerCase())),
                () -> assertEquals(Optional.of(state), BookingState.from("  " + state.name() + "  "))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"UNKNOWN", "wait", "CURENT", "futuree"})
    @NullAndEmptySource
    void from_ShouldReturnEmpty(String input) {
        assertEquals(Optional.empty(), BookingState.from(input));
    }

    @Test
    void from_ShouldEmpty() {
        assertEquals(Optional.empty(),BookingState.from(null));
    }

    @Test
    void from_ShouldTrimInput() {
        assertEquals(Optional.of(BookingState.REJECTED), BookingState.from("  Rejected  "));
    }
}