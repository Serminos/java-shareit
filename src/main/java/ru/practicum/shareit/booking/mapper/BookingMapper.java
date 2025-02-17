package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {

    public BookingDto toItemDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );
    }
    public Booking toItem(BookingDto bookingDto) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStartTime(),
                bookingDto.getEndTime(),
                bookingDto.getItem(),
                bookingDto.getBooker(),
                bookingDto.getStatus()
        );
    }
}
