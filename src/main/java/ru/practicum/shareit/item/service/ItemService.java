package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto add(long userId, ItemDto item);

    ItemDto update(long userId, Long itemId, ItemDto itemDto);

    ItemDto findById(long itemId);

    List<ItemDto> findAllByUserId(long userId);

    List<ItemDto> findAllByText(String text);

}
