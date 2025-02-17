package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.exception.ConflictException;
import ru.practicum.shareit.exception.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Objects;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper mapper) {
        this.userRepository = userRepository;
        this.userMapper = mapper;
    }

    @Override
    public UserDto create(UserDto userDto) {
        isExistByEmail(userDto.getEmail());
        final User user = userRepository.add(userMapper.toUser(userDto));
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto update(long userId, final UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователя с id - [" + userId + "]"));
        isExistByEmail(userDto.getEmail());

        if (Objects.nonNull(userDto.getName())) {
            user.setName(userDto.getName());
        }
        if (Objects.nonNull(userDto.getEmail())) {
            user.setEmail(userDto.getEmail());
        }
        userRepository.update(user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto getUserById(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователя с id - [" + userId + "]"));
        return userMapper.toUserDto(user);
    }

    @Override
    public void removeById(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователя с id - [" + userId + "]"));
    }

    private void isExistByEmail(String email) {
        if (!Objects.nonNull(email)) {
            return;
        }
        for (User user : userRepository.findAll()) {
            if (user.getEmail().equals(email)) {
                throw new ConflictException("Пользователь с email = [" + email + "] уже есть.");
            }
        }
    }
}
