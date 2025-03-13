package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

@Getter
@Setter
@Builder
public class ItemDto {
    Long id;
    String name;
    String description;
    Boolean available;
    Long requestId;
    User owner;
    Request request;
}
