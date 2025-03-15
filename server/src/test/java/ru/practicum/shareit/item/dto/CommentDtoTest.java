package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CommentDtoTest {

    @Test
    void shouldBuildCommentDto() {
        CommentDto dto = CommentDto.builder()
                .id(1L)
                .text("Test comment")
                .authorName("Author")
                .created(LocalDateTime.now())
                .build();

        assertEquals(1L, dto.getId());
        assertEquals("Test comment", dto.getText());
        assertNotNull(dto.getCreated());
    }
}