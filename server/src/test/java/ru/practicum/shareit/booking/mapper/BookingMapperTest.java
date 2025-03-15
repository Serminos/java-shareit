package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.enums.StatusType;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class BookingMapperTest {

    private final BookingMapper mapper = new BookingMapper();

    @Test
    void shouldMapToBookingCorrectly() {
        BookingRequest request = BookingRequest.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .itemId(1L)
                .build();

        User user = new User();
        Item item = new Item();

        Booking booking = mapper.toBooking(request, user, item);

        assertEquals(request.getStart(), booking.getStart());
        assertEquals(user, booking.getBooker());
        assertEquals(StatusType.WAITING, booking.getStatus());
    }

    @Test
    void shouldMapToResponseWithDetails() {
        Booking booking = new Booking();
        booking.setStatus(StatusType.APPROVED);
        UserDto userDto = new UserDto();
        ItemDto itemDto = new ItemDto();

        BookingResponse responce = mapper.toBookingResponce(booking, userDto, itemDto);

        assertEquals(StatusType.APPROVED, responce.getStatus());
        assertSame(userDto, responce.getBooker());
    }
}