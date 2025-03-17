package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponce;

import java.util.List;

public interface ItemService {

    ItemDto add(long userId, ItemDto item);

    ItemDto update(long userId, Long itemId, ItemDto itemDto);

    ItemResponce findById(long ownerId, long itemId);

    List<ItemDto> findAllByUserId(long userId);

    List<ItemDto> findAllByText(String text);

    CommentDto saveComment(long userId, long itemId, CommentDto commentDto);

}
