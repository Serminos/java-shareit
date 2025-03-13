package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

@RestController
@RequestMapping(path = "/requests")
@Validated
@Slf4j
public class RequestController {
    private final RequestClient requestClient;

    public RequestController(RequestClient requestClient) {
        this.requestClient = requestClient;
    }

    @PostMapping
    public ResponseEntity<Object> saveRequest(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long userId,
                                          @RequestBody RequestDto requestDto) {
        log.info("Создание нового запроса вещи пользователем с id = [{}] ", userId);
        return requestClient.save(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUserId(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long userId) {
        log.info("Запрос на получение запросов(вещи) " +
                "вместе с данными об ответах на них пользователя с id = [{}] ", userId);
        return requestClient.getAllByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllExceptUserId(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long userId) {
        log.info("Запрос на получение всех запросов(вещей), кроме тех что сделал пользователь с id = [{}]",
                userId);
        return requestClient.findAllExceptUserId(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getByRequestId(@PathVariable @NotNull @Positive Long requestId,
                                                 @RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long userId) {
        log.info("Запрос на получение данных об одном конкретном запросе(вещи)" +
                " и все ответы на него с id = [{}]", requestId);
        return requestClient.getByRequestId(requestId);
    }
}
