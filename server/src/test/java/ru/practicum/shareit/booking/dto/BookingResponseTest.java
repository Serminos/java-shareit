package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.enums.StatusType;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingResponseTest {

    @Test
    void shouldCreateBookingResponse() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("name item")
                .description("description item")
                .available(true)
                .requestId(1L)
                .build();

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Иванов")
                .email("ivanov@test.ru")
                .build();

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        BookingResponse responce = BookingResponse.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(itemDto)
                .booker(userDto)
                .status(StatusType.APPROVED)
                .build();

        assertEquals(1L, responce.getId());
        assertEquals(itemDto, responce.getItem(), "ItemDto должен совпадать");
        assertEquals(userDto, responce.getBooker(), "UserDto должен совпадать");
        assertEquals(StatusType.APPROVED, responce.getStatus());
        assertEquals(start, responce.getStart());
        assertEquals(end, responce.getEnd());
    }
}