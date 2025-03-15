package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.shareit.validation.CreateObject;

@Getter
@Setter
@Builder
public class ItemDto {
    Long id;
    @NotBlank(groups = {CreateObject.class}, message = "Название вещи не может быть пустым")
    String name;
    @NotBlank(groups = {CreateObject.class}, message = "Описание вещи не может быть пустым")
    String description;
    @NotNull(groups = {CreateObject.class}, message = "Статус вещие не может быть пустым")
    Boolean available;
    Long requestId;
}
