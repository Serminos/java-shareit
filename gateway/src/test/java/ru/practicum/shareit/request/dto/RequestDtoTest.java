package ru.practicum.shareit.request.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class RequestDtoTest {

    @Autowired
    private JacksonTester<RequestDto> jsonTester;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shouldSerializeCorrectly() throws Exception {
        RequestDto dto = new RequestDto();
        dto.setDescription("Need a drill for home repairs");

        JsonContent<RequestDto> result = jsonTester.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Need a drill for home repairs");
    }

    @Test
    void shouldDeserializeFromJson() throws Exception {
        String json = "{\"description\":\"Looking for a camping tent\"}";

        RequestDto dto = jsonTester.parseObject(json);

        assertThat(dto.getDescription()).isEqualTo("Looking for a camping tent");
    }

    @Test
    void shouldPassValidationWithCorrectData() {
        RequestDto dto = new RequestDto();
        dto.setDescription("Valid description");

        Set<ConstraintViolation<RequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailValidationWhenDescriptionIsBlank() {
        RequestDto dto = new RequestDto();
        dto.setDescription("   ");

        Set<ConstraintViolation<RequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .hasSize(1)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("описание не должно быть пустым");
    }

    @Test
    void shouldFailValidationWhenDescriptionIsNull() {
        RequestDto dto = new RequestDto();

        Set<ConstraintViolation<RequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .hasSize(1)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("описание не должно быть пустым");
    }

    @Test
    void lombokAnnotationsShouldWorkCorrectly() {
        RequestDto dto = new RequestDto();
        dto.setDescription("Test description");

        assertThat(dto.getDescription()).isEqualTo("Test description");
        assertThat(dto.toString()).contains("Test description");
    }
}