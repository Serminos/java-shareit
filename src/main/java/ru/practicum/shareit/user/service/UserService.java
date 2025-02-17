package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

@Service
public interface UserService {
    UserDto create(UserDto userDto);

    UserDto update(long userId, UserDto userDto);

    UserDto getUserById(long userId);

    void removeById(long userId);

}
