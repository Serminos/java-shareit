package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.validation.CreateObject;

@Getter
@Setter
@Builder
public class UserDto {
    Long id;
    @NotBlank(groups = {CreateObject.class}, message = "Логин не может быть пустым")
    String name;
    @Email(groups = {CreateObject.class}, message = "Электронная почта не может быть пустой и должна содержать символ @")
    @NotNull(groups = {CreateObject.class}, message = "Электронная почта не может быть пустой и должна содержать символ @")
    String email;
}
