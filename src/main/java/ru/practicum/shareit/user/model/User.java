package ru.practicum.shareit.user.model;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class User {
    Long id;        // уникальный идентификатор пользователя
    String name;    // имя или логин пользователя.
    String email;   // — адрес электронной почты (учтите, что два пользователя не могут
                    // иметь одинаковый адрес электронной почты)
}
