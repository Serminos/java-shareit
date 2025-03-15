package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
@DataJpaTest
class RequestRepositoryTest {
    final LocalDateTime now = LocalDateTime.now().withNano(0);

    @Autowired
    RequestRepository requestRepository;

    @Autowired
    UserRepository userRepository;

    User testUser1;
    User testUser2;

    @BeforeEach
    void setUp() {
        testUser1 = createAndSaveUser("Test1", "test1@test.ru");
        testUser2 = createAndSaveUser("Test2", "test2@test.ru");
    }

    @Test
    void findAll_WhenNoRequests_ReturnsEmptyList() {
        List<Request> result = requestRepository.findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllByRequestorId_WhenRequestsExist_ReturnsUserRequests() {
        Request request1 = createAndSaveRequest("APPLE MAC STUDIO - 2025", testUser1, now);
        Request request2 = createAndSaveRequest("пачка бумаги", testUser1, now.plusHours(1));

        List<Request> result = requestRepository.findAllByRequestorId(testUser1.getId(), sortByCreatedDesc());

        assertThat(result)
                .hasSize(2)
                .extracting(Request::getDescription)
                .containsExactly(request2.getDescription(), request1.getDescription());
    }

    @Test
    void findAllByRequestorIdNot_WhenRequestsExist_ReturnsOtherUsersRequests() {
        createAndSaveRequest("APPLE MAC STUDIO - 2025", testUser1, now);
        Request request2 = createAndSaveRequest("Что-то 1", testUser2, now.plusHours(1));
        Request request3 = createAndSaveRequest("Что-то 2", testUser2, now.plusHours(2));

        List<Request> result = requestRepository.findAllByRequestorIdNot(
                testUser1.getId(),
                sortByCreatedDesc()
        );

        assertThat(result)
                .hasSize(2)
                .extracting(Request::getDescription)
                .containsExactly(request3.getDescription(), request2.getDescription());
    }

    private Sort sortByCreatedDesc() {
        return Sort.by(Sort.Direction.DESC, "created");
    }

    private User createAndSaveUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    private Request createAndSaveRequest(String description, User requestor, LocalDateTime created) {
        Request request = Request.builder()
        .description(description)
        .requestor(requestor)
        .created(created).build();
        return requestRepository.save(request);
    }
}