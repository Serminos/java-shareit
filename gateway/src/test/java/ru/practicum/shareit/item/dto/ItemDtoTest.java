package ru.practicum.shareit.item.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.validation.CreateObject;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ItemDtoTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void itemDto_CreateValidationSuccess() {
        ItemDto dto = ItemDto.builder()
                .name("Лунный модуль")
                .description("Посадочный модуль")
                .available(true)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto, CreateObject.class);
        assertTrue(violations.isEmpty());
    }

    @Test
    void itemDto_ValidationFailsForEmptyName() {
        ItemDto dto = ItemDto.builder()
                .description("Посадочный модуль")
                .available(true)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto, CreateObject.class);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("Название вещи не может быть пустым");
    }

    @Test
    void itemDto_UpdateValidationSkipsCreateConstraints() {
        ItemDto dto = ItemDto.builder().id(1L).build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void itemDto_BuilderSetsAllFields() {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("Скафандр")
                .description("Транспорт для лунных миссий")
                .available(false)
                .requestId(5L)
                .build();

        assertAll(
                () -> assertEquals(1L, dto.getId()),
                () -> assertEquals("Скафандр", dto.getName()),
                () -> assertEquals("Транспорт для лунных миссий", dto.getDescription()),
                () -> assertFalse(dto.getAvailable()),
                () -> assertEquals(5L, dto.getRequestId())
        );
    }
}