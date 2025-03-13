package ru.practicum.shareit.user;

import jakarta.validation.constraints.NotNull;
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

    private final UserClient userClient;


    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    /**
     * Создать пользователя в БД.
     *
     * @param userDto пользователь
     * @return UserDto созданный пользователь.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@Validated(CreateObject.class) @RequestBody UserDto userDto) {
        log.info("Запрос на добавление пользователя ");
        return userClient.save(userDto);
    }

    /**
     * Обновить юзера в БД.
     *
     * @param userDto пользователь
     * @param userId  ID обновляемого пользователя.
     * @return UserDto обновлённый пользователь.
     */
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable @NotNull @Positive long userId,
                   @Validated(UpdateObject.class) @RequestBody UserDto userDto) {
        log.info("Запрос на обновление пользователя [" + userId + "]");
        return userClient.update(userId, userDto);
    }

    /**
     * Получить пользователя по ID.
     *
     * @param userId ID пользователя.
     * @return UserDto - пользователь присутствует в БД.
     * <p>null - пользователя нет в БД.</p>
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable @NotNull @Positive Long userId) {
        log.info("Получить пользователя по ID [" + userId + "]");
        return userClient.getUserById(userId);
    }

    /**
     * Удалить пользователя из БД.
     *
     * @param userId ID удаляемого пользователя.
     */
    @DeleteMapping("/{userId}")
    public void removeById(@PathVariable @NotNull @Positive Long userId) {
        log.info("Запрос на удаление пользователя [" + userId + "]");
        userClient.removeById(userId);
    }
}
