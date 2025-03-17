package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;

import java.util.List;

public interface BookingService {
    BookingResponse saveRequest(BookingRequest bookingRequest, Long userId);

    BookingResponse approved(Long ownerId, Long bookingId, boolean approved);

    BookingResponse findById(Long userId, Long bookingId);

    List<BookingResponse> findAllByUserId(Long userId, String state);

    List<BookingResponse> findAllByOwnerId(Long ownerId, String state);
}
