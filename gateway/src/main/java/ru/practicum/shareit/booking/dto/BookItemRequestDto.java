package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
	private long itemId;
	@FutureOrPresent(message = "Дата начала бронирования не должна быть в прошлом")
	private LocalDateTime start;
	@Future(message = "Дата окончания бронирования должна быть больше текущей")
	private LocalDateTime end;
}
