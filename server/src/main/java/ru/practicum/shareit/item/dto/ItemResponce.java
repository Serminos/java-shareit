package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
