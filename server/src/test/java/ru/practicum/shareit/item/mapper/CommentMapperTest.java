package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class CommentMapperTest {

    private final CommentMapper mapper = new CommentMapper();

    @Test
    void shouldMapToCommentWithUserAndItem() {
        CommentDto dto = new CommentDto();
        dto.setText("Test");
        User user = new User();
        Item item = new Item();

        Comment comment = mapper.toComment(dto, user, item);

        assertEquals("Test", comment.getText());
        assertSame(user, comment.getAuthor());
        assertSame(item, comment.getItem());
    }

    @Test
    void shouldMapToDtoWithAuthorName() {
        User user = new User();
        user.setName("Test User");
        Comment comment = new Comment();
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        CommentDto dto = mapper.toCommentDto(comment);

        assertEquals("Test User", dto.getAuthorName());
    }
}