package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.enums.StatusType;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.exception.BadRequestException;
import ru.practicum.shareit.exception.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponce;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    static final Long USER_ID = 1L;
    static final Long ITEM_ID = 1L;
    static final Long COMMENT_ID = 1L;
    static final Long REQUEST_ID = 1L;
    static final LocalDateTime NOW = LocalDateTime.now();

    ItemService itemService;

    @Mock ItemRepository itemRepository;
    @Mock UserRepository userRepository;
    @Mock CommentRepository commentRepository;
    @Mock BookingRepository bookingRepository;
    @Mock
    RequestRepository requestRepository;

    User owner;
    Item item;
    ItemDto itemDto;
    CommentDto commentDto;
    Request request;

    @BeforeEach
    void setUp() {
        ItemMapper itemMapper = new ItemMapper();
        CommentMapper commentMapper = new CommentMapper();

        itemService = new ItemServiceImpl(
                itemRepository,
                commentRepository,
                bookingRepository,
                userRepository,
                itemMapper,
                commentMapper,
                requestRepository
        );

        owner = createUser(USER_ID, "Owner", "owner@test.com");
        item = createItem(ITEM_ID, "Item", "Description", owner, true, null);
        itemDto = createItemDto("Item", "Description", true);
        commentDto = createCommentDto("Test comment");
        request = createItemRequest(REQUEST_ID, "Request description", owner);
    }

    @Test
    void create_WhenUserNotFound_ThrowsException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.add(USER_ID, itemDto));
    }

    @Test
    void create_WithValidData_ReturnsItem() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.add(USER_ID, itemDto);

        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
    }

    @Test
    void create_WithItemRequest_ReturnsItemWithRequestId() {
        Item itemWithRequest = createItem(ITEM_ID, "Item", "Description", owner, true, request);
        ItemDto requestItemDto = createItemDtoWithRequest(REQUEST_ID);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(owner));
        when(requestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(request));
        when(itemRepository.save(any(Item.class))).thenReturn(itemWithRequest);

        ItemDto result = itemService.add(USER_ID, requestItemDto);

        assertEquals(REQUEST_ID, result.getRequest().getId());
    }

    @Test
    void update_WhenItemNotFound_ThrowsException() {
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.update(USER_ID, ITEM_ID, itemDto));
    }

    @Test
    void update_ByNonOwner_ThrowsNotFoundException() {
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class,
                () -> itemService.update(USER_ID + 1, ITEM_ID, itemDto));
    }

    @Test
    void update_ItemName_ReturnsUpdatedItem() {
        ItemDto updateDto = createItemDto("New Name", null, null);
        Item updatedItem = createItem(ITEM_ID, "New Name", item.getDescription(), owner, true, null);

        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

        ItemDto result = itemService.update(USER_ID, ITEM_ID, updateDto);

        assertEquals("New Name", result.getName());
    }

    @Test
    void findById_WhenItemNotExists_ThrowsNotFoundException() {
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.findById(USER_ID, ITEM_ID));
    }

    @Test
    void findById_WithValidData_ReturnsItemDetails() {
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(ITEM_ID)).thenReturn(List.of());

        ItemResponce result = itemService.findById(USER_ID, ITEM_ID);

        assertNotNull(result);
        assertEquals(item.getName(), result.getName());
    }

    @Test
    void getItemsByOwnerId_ReturnsOwnerItems() {
        when(itemRepository.findAllByOwnerId(USER_ID)).thenReturn(List.of(item));

        List<ItemDto> result = itemService.findAllByUserId(USER_ID);

        assertEquals(1, result.size());
        assertEquals(item.getName(), result.get(0).getName());
    }

    @Test
    void search_WithEmptyText_ReturnsEmptyList() {
        List<ItemDto> result = itemService.findAllByText("");

        assertTrue(result.isEmpty());
    }

    @Test
    void search_WithNormText_ReturnsItem() {
        when(itemRepository.search("description")).thenReturn(List.of(item));

        List<ItemDto> result = itemService.findAllByText(item.getDescription());

        assertEquals(result.size(),1);
    }

    @Test
    void saveComment_WithWrongUser_ThrowsNotFoundException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.saveComment(USER_ID, ITEM_ID, commentDto));
    }

    @Test
    void saveComment_WithWrongItem_ThrowsNotFoundException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.saveComment(USER_ID, ITEM_ID, commentDto));
    }

    @Test
    void saveComment_WithoutBookings_ThrowsNotFoundException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(
                anyLong(), anyLong(), any(StatusType.class), any(LocalDateTime.class)))
                .thenReturn(List.of());

        assertThrows(BadRequestException.class,
                () -> itemService.saveComment(USER_ID, ITEM_ID, commentDto));
    }

    @Test
    void saveComment_WithValidData_ReturnsComment() {
        Booking booking = createBooking(owner, item, StatusType.APPROVED);
        Comment comment = createComment("Test comment", owner, item);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(
                eq(USER_ID), eq(ITEM_ID), eq(StatusType.APPROVED), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = itemService.saveComment(USER_ID, ITEM_ID, commentDto);

        assertEquals(comment.getText(), result.getText());
        assertEquals(owner.getName(), result.getAuthorName());
    }

    private User createUser(Long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private Item createItem(Long id, String name, String description, User owner,
                            boolean available, Request request) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setOwner(owner);
        item.setAvailable(available);
        item.setRequest(request);
        return item;
    }

    private ItemDto createItemDto(String name, String description, Boolean available) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        return dto;
    }

    private ItemDto createItemDtoWithRequest(Long requestId) {
        ItemDto dto = createItemDto("Item", "Description", true);
        dto.setRequestId(requestId);
        return dto;
    }

    private CommentDto createCommentDto(String text) {
        CommentDto dto = new CommentDto();
        dto.setText(text);
        return dto;
    }

    private Request createItemRequest(Long id, String description, User requestor) {
        Request request = new Request();
        request.setId(id);
        request.setDescription(description);
        request.setRequestor(requestor);
        request.setCreated(NOW);
        return request;
    }

    private Booking createBooking(User booker, Item item, StatusType status) {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(status);
        booking.setStart(NOW.minusDays(2));
        booking.setEnd(NOW.minusDays(1));
        return booking;
    }

    private Comment createComment(String text, User author, Item item) {
        Comment comment = new Comment();
        comment.setId(COMMENT_ID);
        comment.setText(text);
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(NOW);
        return comment;
    }
}