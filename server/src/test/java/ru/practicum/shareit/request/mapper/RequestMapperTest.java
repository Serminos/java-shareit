package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestResponseDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RequestMapperTest {

    private final RequestMapper mapper = new RequestMapper();

    @Test
    void toRequestShouldMapCorrectly() {
        RequestDto dto = new RequestDto();
        dto.setDescription("Test");
        User user = new User();
        user.setId(1L);

        Request request = mapper.toRequest(dto, user);

        assertEquals("Test", request.getDescription());
        assertEquals(user, request.getRequestor());
        assertNotNull(request.getCreated());
    }

    @Test
    void toRequestResponseDtoShouldMapCorrectly() {
        Request request = new Request();
        request.setId(1L);
        request.setDescription("Test");
        request.setCreated(LocalDateTime.now());

        RequestResponseDto dto = mapper.toRequestResponseDto(request);

        assertEquals(request.getId(), dto.getId());
        assertEquals(request.getDescription(), dto.getDescription());
        assertEquals(request.getCreated(), dto.getCreated());
    }
}