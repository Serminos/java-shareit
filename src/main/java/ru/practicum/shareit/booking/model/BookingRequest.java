package ru.practicum.shareit.booking.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class BookingRequest {

    @NotBlank
    LocalDateTime start;

    @NotBlank
    LocalDateTime end;

    @NotBlank
    Long itemId;
}
