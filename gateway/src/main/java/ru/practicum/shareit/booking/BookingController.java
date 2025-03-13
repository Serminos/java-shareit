package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;


@Controller
@RequestMapping(path = "/bookings")
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;


    public BookingController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> saveRequest(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long userId,
                                              @RequestBody @Valid BookItemRequestDto bookingRequest) {
        log.info("Запрос на бронирование вещи с id " + bookingRequest.getItemId());
        return bookingClient.saveRequest(userId, bookingRequest);

    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approved(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long ownerId,
                                           @PathVariable Long bookingId,
                                           @RequestParam boolean approved) {
        log.info("Запрос на подтверждение бронирование вещи с id " + bookingId);
        return bookingClient.approved(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long userId,
                                           @PathVariable Long bookingId) {
        log.info("Запрос на получение информации о бронировании вещи с id " + bookingId);
        return bookingClient.findById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUserId(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long userId,
                                        @RequestParam(defaultValue = "ALL") String stateParam) {
        log.info("Запрос на получение всех бронирований пользователя с id " + userId);
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.findAllByUserId(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllByOwnerId(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long ownerId,
                                         @RequestParam(defaultValue = "ALL") String state) {
        log.info("Запрос на получение всех забронированных вещей пользователя с id " + ownerId);
        return bookingClient.findAllByOwnerId(ownerId, state);
    }
}
