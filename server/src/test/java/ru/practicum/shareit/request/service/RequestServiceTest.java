package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestResponseDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {
    static final long USER_ID = 1;
    static final long REQUEST_ID = 1;
    static final LocalDateTime NOW = LocalDateTime.now().withNano(0);

    @Mock
    RequestRepository requestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    ItemMapper itemMapper;

    @InjectMocks
    RequestServiceImpl requestService;

    User requestor;
    RequestDto requestDto;
    Request request1;
    Request request2;
    Item item1;
    Item item2;

    @BeforeEach
    void setUp() {
        RequestMapper requestMapper = new RequestMapper();
        UserMapper userMapper = new UserMapper();

        requestService = new RequestServiceImpl(
                requestRepository,
                requestMapper,
                userRepository,
                userMapper,
                itemRepository,
                itemMapper
        );

        requestor = createUser(USER_ID, "TestRequestor", "TestRequestor@test.ru");
        requestDto = createRequestDto("Mac Studio 2025");

        request1 = createRequest(REQUEST_ID, "Mac Studio 2025", requestor, NOW);
        request2 = createRequest(2L, "Iphone", requestor, NOW.plusHours(1));

        item1 = createItem("Computer", "Super Computer", requestor, request1);
        item2 = createItem("Phone", "Super Phone", requestor, request2);
    }

    @Test
    void saveItemRequest_WhenUserNotFound_ThrowsException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> requestService.save(USER_ID, requestDto));
    }

    @Test
    void saveItemRequest_WithValidData_ReturnsCreatedRequest() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(requestor));
        when(requestRepository.save(any(Request.class))).thenReturn(request1);

        RequestResponseDto result = requestService.save(USER_ID, requestDto);

        assertThat(result)
                .hasFieldOrPropertyWithValue("description", "Mac Studio 2025")
                .hasFieldOrPropertyWithValue("requestor.name", "TestRequestor");

        verify(requestRepository).save(any(Request.class));
    }

    @Test
    void getAllByUserId_WhenRequestsExist_ReturnsRequestsWithItems() {
        when(requestRepository.findAllByRequestorId(USER_ID, sortByCreatedDesc()))
                .thenReturn(List.of(request1, request2));
        when(itemRepository.findAllByRequest(request1)).thenReturn(List.of(item1));
        when(itemRepository.findAllByRequest(request2)).thenReturn(List.of(item2));
        when(userRepository.existsById(USER_ID)).thenReturn(true);

        List<RequestResponseDto> result = requestService.getAllByUserId(USER_ID);

        assertThat(result)
                .hasSize(2)
                .extracting(RequestResponseDto::getDescription)
                .containsExactly("Mac Studio 2025", "Iphone");
    }

    @Test
    void findAllExceptUserId_WhenExceptUser_ReturnsRequests() {
        when(requestRepository.findAllByRequestorIdNot(USER_ID, sortByCreatedDesc()))
                .thenReturn(List.of(request1, request2));
        when(userRepository.existsById(USER_ID)).thenReturn(true);

        List<RequestResponseDto> result = requestService.findAllExceptUserId(USER_ID);

        assertThat(result)
                .hasSize(2)
                .extracting(RequestResponseDto::getDescription)
                .containsExactly("Mac Studio 2025", "Iphone");
    }

    @Test
    void getByRequestId_WhenRequestsExist_ReturnsRequestsWithItems() {
        when(requestRepository.findById(REQUEST_ID))
                .thenReturn(Optional.ofNullable(request1));
        when(itemRepository.findAllByRequest(request1)).thenReturn(List.of(item1));

        RequestResponseDto result = requestService.getByRequestId(REQUEST_ID);

        assertEquals("Mac Studio 2025", result.getDescription());
        assertEquals("TestRequestor", result.getRequestor().getName());
        assertEquals(1, result.getItems().size());
    }

    @Test
    void getByRequestId_WhenRequestNotFound_ThrowsException() {
        assertThrows(
                NotFoundException.class,
                () -> requestService.getByRequestId(100L)
        );
    }

    private Sort sortByCreatedDesc() {
        return Sort.by(Sort.Direction.DESC, "created");
    }

    private User createUser(long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private RequestDto createRequestDto(String description) {
        RequestDto dto = new RequestDto();
        dto.setDescription(description);
        return dto;
    }

    private Request createRequest(Long id, String description, User requestor, LocalDateTime created) {
        Request request = new Request();
        request.setId(id);
        request.setDescription(description);
        request.setRequestor(requestor);
        request.setCreated(created);
        return request;
    }

    private Item createItem(String name, String description, User owner, Request request) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setOwner(owner);
        item.setAvailable(true);
        item.setRequest(request);
        return item;
    }
}