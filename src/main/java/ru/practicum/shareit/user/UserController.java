package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.CreateObject;
import ru.practicum.shareit.validation.UpdateObject;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@Validated
public class UserController {
    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Создать пользователя в БД.
     *
     * @param userDto пользователь
     * @return UserDto созданный пользователь.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Validated(CreateObject.class) @RequestBody final UserDto userDto) {
        log.info("Запрос на добавление пользователя ");
        return userService.create(userDto);
    }

    /**
     * Обновить юзера в БД.
     *
     * @param userDto пользователь
     * @param userId  ID обновляемого пользователя.
     * @return UserDto обновлённый пользователь.
     */
    @PatchMapping("/{userId}")
    UserDto update(@PathVariable long userId,
                   @Validated(UpdateObject.class) @RequestBody UserDto userDto) {
        log.info("Запрос на обновление пользователя [" + userId + "]");
        return userService.update(userId, userDto);
    }

    /**
     * Получить пользователя по ID.
     *
     * @param userId ID пользователя.
     * @return UserDto - пользователь присутствует в БД.
     * <p>null - пользователя нет в БД.</p>
     */
    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    /**
     * Удалить пользователя из БД.
     *
     * @param userId ID удаляемого пользователя.
     */
    @DeleteMapping("/{userId}")
    public void removeById(@PathVariable Long userId) {
        log.info("Запрос на удаление пользователя [" + userId + "]");
        userService.removeById(userId);
    }
}
