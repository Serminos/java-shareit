package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestDtoTest {

    @Test
    void shouldCreateRequestDto() {
        RequestDto dto = new RequestDto();
        dto.setDescription("Test description");

        assertEquals("Test description", dto.getDescription());
    }
}