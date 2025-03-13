package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDtoForRequest {
    Long itemId;
    String name;
    Long ownerId;
}
