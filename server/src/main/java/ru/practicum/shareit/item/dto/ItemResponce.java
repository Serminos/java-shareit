package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class ItemResponce {
    Long id;

    String name;

    String description;

    Boolean available;

    String ownerName;

    LocalDateTime lastBooking;

    LocalDateTime nextBooking;

    List<CommentDto> comments = new ArrayList<>();
}
