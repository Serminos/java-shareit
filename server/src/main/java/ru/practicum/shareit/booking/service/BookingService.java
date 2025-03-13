package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponce;

import java.util.List;

public interface BookingService {
    BookingResponce saveRequest(BookingRequest bookingRequest, Long userId);

    BookingResponce approved(Long ownerId, Long bookingId, boolean approved);

    BookingResponce findById(Long userId, Long bookingId);

    List<BookingResponce> findAllByUserId(Long userId, String state);

    List<BookingResponce> findAllByOwnerId(Long ownerId, String state);
}
