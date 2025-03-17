package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CommentDto {
    Long id;
    @NotBlank(message = "Комментарий не может быть пустым")
    String text;
    String authorName;
    LocalDateTime created;
}
