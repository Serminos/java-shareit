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
import java.util.Optional;

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
        checkIfEmailExists(userDto.getEmail(), null);
        final User user = userRepository.add(userMapper.toUser(userDto));
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto update(long userId, final UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователя с id - [" + userId + "]"));
        checkIfEmailExists(userDto.getEmail(), userDto.getId());

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
        userRepository.removeById(userId);
    }

    private void checkIfEmailExists(String email, Long excludedUserId) {
        if (email == null || email.isEmpty()) {
            return; // Email не указан, проверка не требуется
        }

        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent() && (excludedUserId == null || !existingUser.get().getId().equals(excludedUserId))) {
            throw new ConflictException("Пользователь с email = [" + email + "] уже есть.");
        }
    }
}
