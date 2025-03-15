package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertSame;

class ItemModelTest {

    @Test
    void shouldCreateItemWithOwner() {
        User owner = new User();
        Item item = new Item();
        item.setOwner(owner);

        assertSame(owner, item.getOwner());
    }
}