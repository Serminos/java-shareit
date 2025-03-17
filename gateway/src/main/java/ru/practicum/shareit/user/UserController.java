package ru.practicum.shareit.user;

import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.CreateObject;
import ru.practicum.shareit.validation.UpdateObject;


@RestController
@RequestMapping(path = "/users")
@Validated
@Slf4j
public class UserController {
    private static final String ID_VALIDATION_MSG = "ID пользователя должно быть положительным числом";
    private final UserClient userClient;

    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@Validated(CreateObject.class) @RequestBody UserDto userDto) {
        log.info("Создание пользователя: [{}]", userDto.getEmail());
        return userClient.save(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable @Positive(message = ID_VALIDATION_MSG) long userId,
                                         @Validated(UpdateObject.class) @RequestBody UserDto userDto) {
        log.info("Запрос на обновление пользователя [{}]", userId);
        return userClient.update(userId, userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable @Positive(message = ID_VALIDATION_MSG) Long userId) {
        log.info("Запрос пользователя ID: [{}]", userId);
        return userClient.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeById(@PathVariable @Positive(message = ID_VALIDATION_MSG) Long userId) {
        log.info("Удаление пользователя ID: [{}]", userId);
        userClient.removeById(userId);
    }
}
