package ru.practicum.shareit.booking.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatusTypeTest {

    @Test
    void shouldContainCorrectStatusValues() {
        assertEquals(4, StatusType.values().length);
        assertEquals("WAITING", StatusType.WAITING.name());
        assertEquals("APPROVED", StatusType.APPROVED.name());
        assertEquals("REJECTED", StatusType.REJECTED.name());
        assertEquals("CANCELED", StatusType.CANCELED.name());
    }
}