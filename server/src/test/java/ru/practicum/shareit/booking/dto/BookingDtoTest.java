package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.enums.StatusType;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingDtoTest {

    @Test
    void shouldCreateBookingDtoWithAllArgs() {
        Item item = Item.builder()
                .id(1L)
                .name("name item")
                .description("description item")
                .available(true)
                .owner(User.builder().id(2L).build())
                .request(Request.builder().id(3L).build())
                .build();

        User user = User.builder()
                .id(1L)
                .name("Иванов")
                .email("ivanov@test.ru")
                .build();

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        BookingDto dto = new BookingDto(
                1L,
                start,
                end,
                item,
                user,
                StatusType.WAITING
        );

        assertEquals(1L, dto.getId());
        assertEquals(item, dto.getItem(), "Item должен совпадать");
        assertEquals(user, dto.getBooker(), "Booker должен совпадать");
        assertEquals(StatusType.WAITING, dto.getStatus());

        assertEquals(start, dto.getStart());
        assertEquals(end, dto.getEnd());
    }
}