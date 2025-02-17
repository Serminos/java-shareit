package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item add(Item item);

    Optional<Item> findById(long itemId);

    Item update(long itemId, ItemDto itemDto);

    List<Item> findAllByUserId(long itemId);

    List<Item> findAllByText(String text);
}
