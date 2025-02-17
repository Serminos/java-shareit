package ru.practicum.shareit.item.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Qualifier("InMemory")
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private Long count = 0L;

    @Override
    public Item add(Item item) {
        item.setId(++count);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> findById(long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Item update(final long itemId, final ItemDto itemDto) {
        final Item item = items.get(itemId);
        if (Objects.nonNull(itemDto.getName())) {
            item.setName(itemDto.getName());
        }
        if ((Objects.nonNull(itemDto.getDescription()))) {
            item.setDescription(itemDto.getDescription());
        }
        if ((Objects.nonNull(itemDto.getAvailable()))) {
            item.setAvailable(itemDto.getAvailable());
        }
        items.put(itemId, item);
        return item;
    }

    @Override
    public List<Item> findAllByUserId(long userId) {
        return items.values().stream().filter(i -> i.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findAllByText(String text) {
        return items.values().stream()
                .filter(item -> (item.getName().equalsIgnoreCase(text.toLowerCase()) ||
                        item.getDescription().equalsIgnoreCase(text.toLowerCase())))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }
}
