package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Getter
@Setter
@Builder
public class Item {
    Long id;            // уникальный идентификатор вещи
    String name;        // краткое название.
    String description; // развёрнутое описание.
    Boolean available;  // статус о том, доступна или нет вещь для аренды
    User owner;         // владелец вещи
    ItemRequest request;     // если вещь была создана по запросу другого пользователя, то в этом
                             // поле будет храниться ссылка на соответствующий запрос.
}
