package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.enums.StatusType;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDto {
    Long id;
    LocalDateTime startTime;
    LocalDateTime endTime;
    Item item;
    User booker;
    StatusType status;
}
