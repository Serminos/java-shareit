package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(long userId);

    Optional<User> findByEmail(String email);

    void removeById(long userId);

    User add(User user);

    User update(User user);

    List<User> findAll();
}
