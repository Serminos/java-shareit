package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.CreateObject;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ItemDto {
    Long id;            // уникальный идентификатор вещи
    @NotBlank(groups = {CreateObject.class}, message = "Название вещи не может быть пустым")
    String name;        // краткое название.
    @NotBlank(groups = {CreateObject.class}, message = "Описание вещи не может быть пустым")
    String description; // развёрнутое описание.
    @NotNull(groups = {CreateObject.class}, message = "Статус вещие не может быть пустым")
    Boolean available;  // статус о том, доступна или нет вещь для аренды
    User owner;         // владелец вещи
    ItemRequest request;    // если вещь была создана по запросу другого пользователя, то в этом
                            // поле будет храниться ссылка на соответствующий запрос.
}
