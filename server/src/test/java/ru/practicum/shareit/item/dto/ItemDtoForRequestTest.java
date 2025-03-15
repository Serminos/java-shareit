package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemDtoForRequestTest {

    @Test
    void shouldSetAndGetFields() {
        ItemDtoForRequest dto = new ItemDtoForRequest();
        dto.setItemId(1L);
        dto.setName("Item");
        dto.setOwnerId(2L);

        assertEquals(1L, dto.getItemId());
        assertEquals("Item", dto.getName());
        assertEquals(2L, dto.getOwnerId());
    }
}