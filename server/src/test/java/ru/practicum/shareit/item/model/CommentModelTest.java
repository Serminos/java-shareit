package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertSame;

class CommentModelTest {

    @Test
    void shouldCreateCommentWithRelations() {
        User author = new User();
        Item item = new Item();
        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setItem(item);

        assertSame(author, comment.getAuthor());
        assertSame(item, comment.getItem());
    }
}