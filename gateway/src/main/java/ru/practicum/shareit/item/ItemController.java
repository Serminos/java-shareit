package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.CreateObject;

@RestController
@RequestMapping("/items")
@Validated
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    /**
     * Добавление новой вещи.
     * Будет происходить по эндпоинту POST /items. На вход поступает объект ItemDto. userId в
     * заголовке X-Sharer-User-Id — это идентификатор пользователя, который добавляет вещь. Именно этот пользователь —
     * владелец вещи. Идентификатор владельца будет поступать на вход в каждом из запросов, рассмотренных далее.
     *
     * @param userId  ИД владельца
     * @param itemDto вещь
     * @return добавленная в БД вещь.
     */
    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(value = "X-Sharer-User-Id", required = false)  @NotNull @Positive Long userId,
                                      @Validated(CreateObject.class) @RequestBody ItemDto itemDto) {
        log.info("Запрос на добавление новой вещи");
        return itemClient.add(userId, itemDto);
    }

    /**
     * Редактирование вещи.
     * Эндпоинт PATCH /items/{itemId}.
     * Изменить можно название, описание и статус доступа к аренде.
     * Редактировать вещь может только её владелец.
     *
     * @param userId  ИД владельца
     * @param itemId  ИД вещи
     * @param itemDto вещь
     * @return ItemDto
     */
    @PatchMapping("{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                         @PathVariable @NotNull @Positive Long itemId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Запрос на обновление вещи");
        return itemClient.update(userId, itemId, itemDto);
    }

    /**
     * Просмотр информации о конкретной вещи по её идентификатору. Эндпоинт GET /items/{itemId}.
     * Информацию о вещи может просмотреть любой пользователь.
     *
     * @param userId ИД владельца
     * @param itemId
     * @return ItemDto
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long userId,
                                              @PathVariable @NotNull @Positive Long itemId) {
        return itemClient.getItemById(userId, itemId);
    }

    /**
     * Просмотр владельцем списка всех его вещей с указанием названия и описания для каждой из них.
     * Эндпоинт GET /items.
     *
     * @param userId
     * @return List<ItemDto>
     */
    @GetMapping()
    public ResponseEntity<Object> findAllByUserId(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long userId) {
        return itemClient.getItemsByOwnerId(userId);
    }

    /**
     * Поиск вещи потенциальным арендатором.
     * Пользователь передаёт в строке запроса текст, и система ищет вещи, содержащие этот текст в названии или описании.
     * Происходит по эндпоинту /items/search?text={text}, в text передаётся текст для поиска.
     * Проверьте, что поиск возвращает только доступные для аренды вещи.
     *
     * @param text
     * @return List<ItemDto>
     */
    @GetMapping("/search")
    public ResponseEntity<Object> findAllByText(@RequestParam(value = "text", required = true) @NotNull String text) {
        return itemClient.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> saveComment(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long userId,
                                              @PathVariable @NotNull @Positive Long itemId,
                                              @Valid @RequestBody CommentDto commentDto) {
        log.info("Пользователь с id {} отправил запрос с комментарием к вещи с id {} ", userId, itemId);
        return itemClient.saveComment(userId, itemId, commentDto);
    }
}
