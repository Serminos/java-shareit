package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RequestResponseDtoTest {

    @Test
    void shouldBuildRequestResponseDto() {
        RequestResponseDto dto = RequestResponseDto.builder()
                .id(1L)
                .description("Test")
                .created(LocalDateTime.now())
                .requestor(UserDto.builder().id(1L).build())
                .build();

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertNotNull(dto.getCreated());
    }
}