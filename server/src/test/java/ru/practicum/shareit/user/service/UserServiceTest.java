package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    static final long USER_ID = 1;
    static final String USER_NAME = "Tester";
    static final String USER_EMAIL = "test@test.ru";
    static final String UPDATED_NAME = "Updated Tester";
    static final String UPDATED_EMAIL = "updated@mail.ru";
    @Spy
    UserMapper userMapper = new UserMapper();
    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserServiceImpl userService;

    User user;
    UserDto userDto;

    @BeforeEach
    void setUp() {
        user = createUser(USER_ID, USER_NAME, USER_EMAIL);
        userDto = createUserDto(USER_NAME, USER_EMAIL);
    }

    @Test
    void create_WithValidData_ReturnsSavedUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.create(userDto);

        assertUserEquals(user, result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void getUserById_WithExistingId_ReturnsUser() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(USER_ID);

        assertUserEquals(user, result);
    }

    @Test
    void getUserById_WithNonExistingId_ThrowsNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.getUserById(999));
    }

    @Test
    void update_WhenUpdatingName_ReturnsUpdatedUser() {
        UserDto updateDto = createUserDto(UPDATED_NAME, null);
        User updatedUser = createUser(USER_ID, UPDATED_NAME, USER_EMAIL);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto result = userService.update(USER_ID, updateDto);

        assertEquals(UPDATED_NAME, result.getName());
        assertEquals(USER_EMAIL, result.getEmail());
    }

    @Test
    void update_WhenUpdatingEmail_ReturnsUpdatedUser() {
        UserDto updateDto = createUserDto(null, UPDATED_EMAIL);
        User updatedUser = createUser(USER_ID, USER_NAME, UPDATED_EMAIL);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto result = userService.update(USER_ID, updateDto);

        assertEquals(USER_NAME, result.getName());
        assertEquals(UPDATED_EMAIL, result.getEmail());
    }

    @Test
    void update_WhenUserNotFound_ThrowsNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.update(999, userDto));
    }


    @Test
    void removeById_WithExistingUser_CallsRepository() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        userService.removeById(USER_ID);

        verify(userRepository).deleteById(USER_ID);
    }

    @Test
    void removeById_WithNonExistingUser_ThrowsNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.removeById(999));
    }

    private User createUser(long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private UserDto createUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }

    private void assertUserEquals(User expected, UserDto actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmail(), actual.getEmail());
    }
}