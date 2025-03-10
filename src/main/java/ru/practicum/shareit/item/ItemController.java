package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponce;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.CreateObject;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
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
    public ItemDto add(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                       @Validated(CreateObject.class) @RequestBody ItemDto itemDto) {
        log.info("Запрос на добавление новой вещи");
        return itemService.add(userId, itemDto);
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
    public ItemDto update(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                          @PathVariable Long itemId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Запрос на обновление вещи");
        return itemService.update(userId, itemId, itemDto);
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
    public ItemResponce getItemById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        return itemService.findById(userId, itemId);
    }

    /**
     * Просмотр владельцем списка всех его вещей с указанием названия и описания для каждой из них.
     * Эндпоинт GET /items.
     *
     * @param userId
     * @return List<ItemDto>
     */
    @GetMapping()
    public List<ItemDto> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.findAllByUserId(userId);
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
    public List<ItemDto> findAllByText(@RequestParam(value = "text", required = false) String text) {
        return itemService.findAllByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto saveComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long itemId,
                                  @Valid @RequestBody CommentDto commentDto) {
        log.info("Пользователь с id {} отправил запрос с комментарием к вещи с id {} ", userId, itemId);
        return itemService.saveComment(userId, itemId, commentDto);
    }
}
