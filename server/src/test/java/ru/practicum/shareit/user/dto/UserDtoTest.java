package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserDtoTest {
    @Test
    void testUserDtoBuilder() {
        UserDto dto = UserDto.builder()
                .id(1L)
                .name("Test")
                .email("test@test.ru")
                .build();

        assertEquals(1L, dto.getId());
        assertEquals("Test", dto.getName());
        assertEquals("test@test.ru", dto.getEmail());
    }
}