package ru.practicum.shareit.item.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentDtoTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void commentDto_BuilderWorksCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        CommentDto dto = CommentDto.builder()
                .id(1L)
                .text("Проблема с подачей газа")
                .authorName("Ольга Инженерова")
                .created(now)
                .build();

        assertAll(
                () -> assertEquals(1L, dto.getId()),
                () -> assertEquals("Проблема с подачей газа", dto.getText()),
                () -> assertEquals("Ольга Инженерова", dto.getAuthorName()),
                () -> assertEquals(now, dto.getCreated())
        );
    }

    @Test
    void commentDto_ValidationFailsWhenTextBlank() {
        CommentDto dto = CommentDto.builder().text("  ").build();

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Комментарий не может быть пустым");
    }
}