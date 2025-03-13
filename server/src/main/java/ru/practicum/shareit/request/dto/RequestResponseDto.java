package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Component
@Builder
public class RequestResponseDto {
    Long id;
    String description;
    LocalDateTime created;
    UserDto requestor;
    List<ItemDtoForRequest> items;
}

