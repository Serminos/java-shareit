package ru.practicum.shareit.user.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
@Qualifier("InMemory")
public class UserRepositoryImpl implements UserRepository {
    Map<Long, User> users = new HashMap<>();
    private Long count = 0L;

    @Override
    public Optional<User> findById(long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public void removeById(long userId) {
        users.remove(userId);
    }


    @Override
    public User add(User user) {
        user.setId(++count);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
}
