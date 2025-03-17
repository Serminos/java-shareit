package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ItemResponceTest {

    @Test
    void shouldBuildItemResponseWithBookings() {
        ItemResponce response = ItemResponce.builder()
                .id(1L)
                .lastBooking(LocalDateTime.now())
                .nextBooking(LocalDateTime.now().plusDays(1))
                .comments(List.of(new CommentDto()))
                .build();

        assertEquals(1L, response.getId());
        assertEquals(1, response.getComments().size());
        assertNotNull(response.getLastBooking());
    }
}