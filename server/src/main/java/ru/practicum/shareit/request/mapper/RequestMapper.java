package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestResponseDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Component
public class RequestMapper {

    public Request toRequest(RequestDto requestDto, User user) {
        return Request.builder()
                .description(requestDto.getDescription())
                .created(LocalDateTime.now())
                .requestor(user)
                .build();

    }

    public RequestResponseDto toRequestResponseDto(Request request) {
        return RequestResponseDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .build();

    }
}
