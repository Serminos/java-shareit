package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private static final String ID_VALIDATION_MSG = "должно быть положительным числом";
    private final ItemClient itemClient;

    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(USER_ID_HEADER)
                                      @Positive(message = "User ID " + ID_VALIDATION_MSG)
                                      Long userId,
                                      @Validated(CreateObject.class) @RequestBody ItemDto itemDto) {
        log.info("Создание вещи пользователем ID: [{}]", userId);
        return itemClient.add(userId, itemDto);
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(USER_ID_HEADER)
                                         @Positive(message = "User ID " + ID_VALIDATION_MSG)
                                         Long userId,
                                         @PathVariable
                                         @Positive(message = "Item ID " + ID_VALIDATION_MSG) Long itemId,
                                         @Valid @RequestBody ItemDto itemDto) {
        log.info("Обновление вещи ID: [{}] пользователем ID: [{}]", itemId, userId);
        return itemClient.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(USER_ID_HEADER)
                                              @Positive(message = "User ID " + ID_VALIDATION_MSG) Long userId,

                                              @PathVariable
                                              @Positive(message = "Item ID " + ID_VALIDATION_MSG) Long itemId) {
        log.info("Запрос вещи ID: [{}] пользователем ID: [{}]", itemId, userId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping()
    public ResponseEntity<Object> findAllByUserId(@RequestHeader(USER_ID_HEADER)
                                                  @Positive(message = "User ID " + ID_VALIDATION_MSG)
                                                  Long userId) {
        log.info("Запрос всех вещей пользователя ID: [{}]", userId);
        return itemClient.getItemsByOwnerId(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findAllByText(@RequestParam @NotBlank String text) {
        log.info("Поиск по тексту: [{}]", text);
        return itemClient.search(text);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> saveComment(@RequestHeader(USER_ID_HEADER)
                                              @Positive(message = "User ID " + ID_VALIDATION_MSG)
                                              Long userId,

                                              @PathVariable @Positive(message = "Item ID " + ID_VALIDATION_MSG)
                                              Long itemId,
                                              @Valid @RequestBody CommentDto commentDto) {
        log.info("Добавление комментария к вещи ID: [{}] пользователем ID: [{}]", itemId, userId);
        return itemClient.saveComment(userId, itemId, commentDto);
    }
}
