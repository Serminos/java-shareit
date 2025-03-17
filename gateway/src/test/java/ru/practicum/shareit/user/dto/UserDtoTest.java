package ru.practicum.shareit.user.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.groups.Default;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ru.practicum.shareit.validation.CreateObject;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = LocalValidatorFactoryBean.class)
class UserDtoTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validUserDto_ShouldPassValidation() {
        UserDto user = UserDto.builder()
                .name("Федот Простак")
                .email("fedotSuper@test.ru")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user, CreateObject.class);
        assertTrue(violations.isEmpty());
    }

    @Test
    void builderAndGetters_ShouldWorkCorrectly() {
        Long id = 1L;
        String name = "Аркадий";
        String email = "Аркадий@test.ru";

        UserDto user = UserDto.builder()
                .id(id)
                .name(name)
                .email(email)
                .build();

        assertAll(
                () -> assertEquals(id, user.getId()),
                () -> assertEquals(name, user.getName()),
                () -> assertEquals(email, user.getEmail())
        );
    }

    @Test
    void emptyName_ShouldFailCreateValidation() {
        UserDto user = UserDto.builder()
                .email("test@test.ru")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user, CreateObject.class);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("Логин не может быть пустым");
    }

    @Test
    void invalidEmail_ShouldFailCreateValidation() {
        UserDto user = UserDto.builder()
                .name("Петя")
                .email("Петя-инвалид-email")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user, CreateObject.class);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("Электронная почта не может быть пустой и должна содержать символ @");
    }

    @Test
    void nullEmail_ShouldFailCreateValidation() {
        UserDto user = UserDto.builder()
                .name("Вася")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user, CreateObject.class);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("Электронная почта не может быть пустой и должна содержать символ @");
    }

    @Test
    void updateValidation_ShouldIgnoreCreateConstraints() {
        UserDto user = UserDto.builder().build();
        Set<ConstraintViolation<UserDto>> violations = validator.validate(user, Default.class);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whitespaceName_ShouldFailValidation() {
        UserDto user = UserDto.builder()
                .name("   ")
                .email("space@test.com")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user, CreateObject.class);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("Логин не может быть пустым");
    }

    @Test
    void emailWithoutAt_ShouldFailValidation() {
        UserDto user = UserDto.builder()
                .name("David")
                .email("no-at.com")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user, CreateObject.class);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("Электронная почта не может быть пустой и должна содержать символ @");
    }
}