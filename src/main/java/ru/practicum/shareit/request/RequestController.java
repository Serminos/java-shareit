package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestResponseDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@Validated
public class RequestController {
    private final RequestService itemRequestService;

    public RequestController(RequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public RequestResponseDto saveRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestBody RequestDto requestDto) {
        log.info("Создание нового запроса вещи пользователем с id = [{}] ", userId);
        return itemRequestService.save(userId, requestDto);
    }

    @GetMapping
    public List<RequestResponseDto> getAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение запросов(вещи) " +
                "вместе с данными об ответах на них пользователя с id = [{}] ", userId);
        return itemRequestService.getAllByUserId(userId);
    }

    @GetMapping("/all")
    public List<RequestResponseDto> findAllExceptUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение всех запросов(вещей), кроме тех что сделал пользователь с id = [{}]",
                userId);
        return itemRequestService.findAllExceptUserId(userId);
    }

    @GetMapping("/{requestId}")
    public RequestResponseDto getByRequestId(@PathVariable Long requestId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение данных об одном конкретном запросе(вещи)" +
                " и все ответы на него с id = [{}]", requestId);
        return itemRequestService.getByRequestId(requestId, userId);
    }
}
