package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Booking {
    Long id;                    // уникальный идентификатор бронирования
    LocalDateTime startTime;    // дата и время начала бронирования
    LocalDateTime endTime;      //  дата и время конца бронирования
    Item item;                // вещь, которую пользователь бронирует;
    User booker;                // пользователь, который осуществляет бронирование
    Long status;                // статус бронирования

    // WAITING — новое бронирование, ожидает одобрения
    // APPROVED — бронирование подтверждено владельцем
    // REJECTED — бронирование    отклонено владельцем
    // CANCELED — бронирование отменено создателем
}
