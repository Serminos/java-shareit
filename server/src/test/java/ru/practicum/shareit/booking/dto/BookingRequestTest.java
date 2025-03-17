package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingRequestTest {

    @Test
    void shouldBuildBookingRequest() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        BookingRequest request = BookingRequest.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .build();

        assertEquals(start, request.getStart());
        assertEquals(end, request.getEnd());
        assertEquals(1L, request.getItemId());
    }
}