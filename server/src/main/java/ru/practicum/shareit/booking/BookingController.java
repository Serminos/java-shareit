package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse saveRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @RequestBody BookingRequest bookingRequest) {
        log.info("Запрос на бронирование вещи с id " + bookingRequest.getItemId());
        return bookingService.saveRequest(bookingRequest, userId);

    }

    @PatchMapping("/{bookingId}")
    public BookingResponse approved(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                    @PathVariable Long bookingId,
                                    @RequestParam boolean approved) {
        log.info("Запрос на подтверждение бронирование вещи с id " + bookingId);
        return bookingService.approved(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponse findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long bookingId) {
        log.info("Запрос на получение информации о бронировании вещи с id " + bookingId);
        return bookingService.findById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponse> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(defaultValue = "ALL") String state) {
        log.info("Запрос на получение всех бронирований пользователя с id " + userId);
        return bookingService.findAllByUserId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponse> findAllByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                  @RequestParam(defaultValue = "ALL") String state) {
        log.info("Запрос на получение всех забронированных вещей пользователя с id " + ownerId);
        return bookingService.findAllByOwnerId(ownerId, state);
    }
}
