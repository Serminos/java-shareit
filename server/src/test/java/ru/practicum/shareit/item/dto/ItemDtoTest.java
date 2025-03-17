package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ItemDtoTest {

    @Test
    void shouldCreateItemDtoWithAllFields() {
        User owner = new User();
        Request request = new Request();

        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setRequest(request);
        dto.setOwner(owner);

        assertEquals(1L, dto.getId());
        assertSame(owner, dto.getOwner());
        assertSame(request, dto.getRequest());
    }
}