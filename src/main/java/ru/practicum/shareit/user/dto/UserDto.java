package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.validation.CreateObject;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDto {
    Long id;        // уникальный идентификатор пользователя
    @NotBlank(groups = {CreateObject.class}, message = "Логин не может быть пустым")
    String name;    // имя или логин пользователя.
    @Email(groups = {CreateObject.class}, message = "Электронная почта не может быть пустой и должна содержать символ @")
    @NotNull(groups = {CreateObject.class}, message = "Электронная почта не может быть пустой и должна содержать символ @")
    String email;   // — адрес электронной почты
}
