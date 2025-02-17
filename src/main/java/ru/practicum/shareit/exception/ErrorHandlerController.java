package ru.practicum.shareit.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.exception.BadRequestException;
import ru.practicum.shareit.exception.exception.ConflictException;
import ru.practicum.shareit.exception.exception.ErrorResponse;
import ru.practicum.shareit.exception.exception.NotFoundException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.user.UserController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice(assignableTypes = {ItemController.class, UserController.class})
public class ErrorHandlerController {
    private static final Logger log = LoggerFactory.getLogger(ErrorHandlerController.class);

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        String metodName = Objects.requireNonNull(ex.getParameter().getMethod()).getName();
        log.info("Ошибка валидации данных [{}] - [{}]", metodName, errors);
        return errors;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse handleValidationExceptions(NotFoundException e) {
        log.info("Ошибка сервера: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ErrorResponse handleValidationExceptions(BadRequestException e) {
        log.info("Ошибка сервера: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ConflictException.class)
    public ErrorResponse handleValidationExceptions(ConflictException e) {
        log.info("Ошибка сервера: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception e) {
        log.info("Ошибка сервера: {}", e.getMessage());
        return new ErrorResponse("Ошибка сервера: " + e.getMessage());
    }
}