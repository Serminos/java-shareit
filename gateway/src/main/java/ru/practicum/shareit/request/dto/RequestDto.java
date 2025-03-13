package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequestDto {
    @NotBlank(message = "описание не должно быть пустым")
    String description;
}
