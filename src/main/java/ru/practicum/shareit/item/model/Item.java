package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Getter
@Setter
@Builder
public class Item {
    Long id;
    String name;
    String description;
    Boolean available;
    User owner;
    ItemRequest request;
}
