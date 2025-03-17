package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;


@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@Validated
public class BookingController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private static final String ID_VALIDATION_MSG = "должно быть положительным числом";
    private final BookingClient bookingClient;

    public BookingController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> saveRequest(@RequestHeader(USER_ID_HEADER)
                                              @Positive(message = "User ID " + ID_VALIDATION_MSG)
                                              Long userId,

                                              @RequestBody @Valid BookItemRequestDto bookingRequest) {
        log.info("Создание бронирования для вещи ID: [{}] пользователем ID: [{}]",
                bookingRequest.getItemId(), userId);
        return bookingClient.saveRequest(userId, bookingRequest);

    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approved(@RequestHeader(USER_ID_HEADER)
                                           @Positive(message = "User ID " + ID_VALIDATION_MSG)
                                           Long ownerId,

                                           @PathVariable
                                           @Positive(message = "Booking ID " + ID_VALIDATION_MSG) Long bookingId,
                                           @RequestParam boolean approved) {
        log.info("Изменение статуса бронирования ID: [{}] на [{}] владельцем ID: [{}]",
                bookingId, approved, ownerId);
        return bookingClient.approved(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findById(@RequestHeader(USER_ID_HEADER)
                                           @Positive(message = "User ID " + ID_VALIDATION_MSG)
                                           Long userId,
                                           @PathVariable
                                           @Positive(message = "Booking ID " + ID_VALIDATION_MSG)
                                           Long bookingId) {
        log.info("Запрос бронирования ID: [{}] пользователем ID: [{}]", bookingId, userId);
        return bookingClient.findById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUserId(@RequestHeader(USER_ID_HEADER)
                                                  @Positive(message = "User ID " + ID_VALIDATION_MSG)
                                                  Long userId,
                                                  @RequestParam(defaultValue = "ALL") String state) {
        log.info("Запрос бронирований пользователя ID: [{}] в состоянии [{}]", userId, state);
        BookingState bookingState = parseState(state);
        return bookingClient.findAllByUserId(userId, bookingState);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllByOwnerId(@RequestHeader(USER_ID_HEADER) @NotNull @Positive Long ownerId,
                                                   @RequestParam(defaultValue = "ALL") String state) {
        log.info("Запрос бронирований владельца ID: [{}] в состоянии [{}]", ownerId, state);
        BookingState bookingState = parseState(state);
        return bookingClient.findAllByOwnerId(ownerId, bookingState);
    }

    private BookingState parseState(String state) {
        return BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный статус: " + state));
    }
}
