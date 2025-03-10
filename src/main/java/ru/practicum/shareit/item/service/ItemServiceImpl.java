package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.exception.BadRequestException;
import ru.practicum.shareit.exception.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper mapper;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, ItemMapper mapper) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    /**
     * Добавить вещь в репозиторий.
     *
     * @param userId  ID владельца вещи.
     * @param itemDto добавленная вещь.
     * @return добавленная вещь.
     */
    @Override
    public ItemDto add(long userId, ItemDto itemDto) {
        final User owner = getUserByIdOrException(userId);
        itemDto.setOwner(owner);
        Item item = mapper.toItem(itemDto);
        return mapper.toItemDto(itemRepository.add(item));
    }

    /**
     * Обновить вещь в репозитории.
     *
     * @param userId  ID владельца вещи.
     * @param itemId  ID вещи.
     * @param itemDto добавленная вещь.
     * @return добавленная вещь.
     */
    @Override
    public ItemDto update(long userId, Long itemId, ItemDto itemDto) {
        getUserByIdOrException(userId);
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id - [" + itemId + "]"));
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new BadRequestException("Только владелец может редактировать данные о вещи." +
                    "Пользователь с id = [" + userId + "] не владелец вещи с id - [" + itemId + "]");
        }
        final Item newItem = itemRepository.update(itemId, itemDto);
        return mapper.toItemDto(newItem);
    }

    /**
     * Найти по ID вещи
     *
     * @param itemId
     * @return
     */
    @Override
    public ItemDto findById(long itemId) {
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id - [" + itemId + "]"));
        return mapper.toItemDto(item);
    }

    /**
     * Найти по ID пользователя вещей
     *
     * @param userId
     * @return
     */
    @Override
    public List<ItemDto> findAllByUserId(long userId) {
        final List<Item> items = itemRepository.findAllByUserId(userId);
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            itemDtos.add(mapper.toItemDto(item));
        }
        return itemDtos;
    }

    /**
     * Найти по text система ищет вещи, содержащие этот текст в названии или описании
     *
     * @param text
     * @return
     */
    @Override
    public List<ItemDto> findAllByText(String text) {
        final List<Item> items = itemRepository.findAllByText(text);
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            itemDtos.add(mapper.toItemDto(item));
        }
        return itemDtos;
    }

    private User getUserByIdOrException(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователя с id - [{}]" + userId));
    }
}
