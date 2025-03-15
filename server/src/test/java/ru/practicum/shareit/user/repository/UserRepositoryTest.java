package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void save_shouldSaveUser() {
        User user = User.builder()
                .id(1000L)
                .name("Иванов")
                .email("ivanov@test.ru")
                .build();

        User savedUser = userRepository.save(user);
        assertNotNull(savedUser.getId());
        assertEquals(user.getName(), savedUser.getName());
        assertEquals(user.getEmail(), savedUser.getEmail());
    }

    @Test
    void findAll_WhenNoBookings_ShouldReturnEmptyList() {
        List<User> bookings = userRepository.findAll();

        assertTrue(bookings.isEmpty());
    }
}