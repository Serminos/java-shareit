package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.enums.StatusType;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.exception.BadRequestException;
import ru.practicum.shareit.exception.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingServiceTest {
    static final Long USER_ID = 1L;
    static final Long USER_ID_2 = 2L;
    static final Long ITEM_ID = 1L;
    static final Long ITEM_ID_2 = 2L;
    static final Long BOOKING_ID = 1L;
    static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "start");
    static final LocalDateTime NOW = LocalDateTime.now();

    BookingService bookingService;
    BookingMapper bookingMapper = new BookingMapper();

    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;

    User owner;
    User otherOwner;
    Item availableItem;
    Item availableItemOtherOwner;
    Item unavailableItem;
    BookingRequest validBookingRequest;
    BookingRequest validBookingRequest2;
    BookingRequest inValidBookingRequest;
    Booking waitingBooking;
    Booking waitingBookingOtherOwner;
    Booking canceledBooking;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(
                bookingRepository,
                bookingMapper,
                userRepository,
                new UserMapper(),
                itemRepository,
                new ItemMapper()
        );

        owner = createUser("Owner", "owner@test.com");
        otherOwner = createUser("otherOwner", "otherOwner@test.com");
        User booker = createUser("Booker", "booker@test.com");

        availableItem = createItem(ITEM_ID, "Available Item", "Description", owner, true);
        availableItemOtherOwner = createItem(ITEM_ID_2, "Available Item OtherOwner", "Description", otherOwner, true);
        unavailableItem = createItem(ITEM_ID, "Unavailable Item", "Description", owner, false);

        validBookingRequest = createBookingRequest(ITEM_ID, NOW.plusDays(2), NOW.plusDays(5));
        validBookingRequest2 = createBookingRequest(ITEM_ID_2, NOW.plusDays(2), NOW.plusDays(5));
        inValidBookingRequest = createBookingRequest(ITEM_ID_2, NOW.plusDays(5), NOW.minusDays(5));

        waitingBooking = createBooking(
                BOOKING_ID,
                availableItem,
                booker,
                StatusType.WAITING,
                NOW.plusDays(2),
                NOW.plusDays(5)
        );
        waitingBookingOtherOwner = createBooking(
                BOOKING_ID,
                availableItemOtherOwner,
                booker,
                StatusType.WAITING,
                NOW.plusDays(2),
                NOW.plusDays(5)
        );
        canceledBooking = createBooking(
                BOOKING_ID + 1,
                availableItem,
                booker,
                StatusType.CANCELED,
                NOW.minusDays(5),
                NOW.minusDays(2)
        );
    }

    @Test
    void create_WhenUserNotFound_ThrowsNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.saveRequest(validBookingRequest, USER_ID));
    }

    @Test
    void create_WhenItemNotFound_ThrowsNotFoundException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.saveRequest(validBookingRequest, USER_ID));
    }

    @Test
    void create_WhenItemUnavailable_ThrowsValidationException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(unavailableItem));

        assertThrows(BadRequestException.class,
                () -> bookingService.saveRequest(validBookingRequest, USER_ID));
    }

    @Test
    void create_WithInvalidTimeRange_ThrowsValidationException() {
        BookingRequest invalidRequest = createBookingRequest(
                ITEM_ID,
                NOW.plusDays(5),
                NOW.plusDays(2)
        );

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(availableItem));

        assertThrows(BadRequestException.class,
                () -> bookingService.saveRequest(invalidRequest, USER_ID));
    }

    @Test
    void create_WithValidData_ReturnsSavedBooking() {
        when(userRepository.findById(USER_ID_2)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(ITEM_ID_2)).thenReturn(Optional.of(availableItemOtherOwner));
        when(bookingRepository.save(any(Booking.class))).thenReturn(waitingBookingOtherOwner);

        BookingResponse response = bookingService.saveRequest(validBookingRequest2, USER_ID_2);

        assertNotNull(response);
        assertEquals(StatusType.WAITING, response.getStatus());
        assertEquals(availableItemOtherOwner.getName(), response.getItem().getName());
    }

    @Test
    void create_WithUnavailableItem_ReturnsSavedBooking() {
        when(userRepository.findById(USER_ID_2)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(ITEM_ID_2)).thenReturn(Optional.of(unavailableItem));

        assertThrows(BadRequestException.class,
                () -> bookingService.saveRequest(validBookingRequest2, USER_ID_2));
    }

    @Test
    void create_WithBadDate_ReturnsSavedBooking() {
        when(userRepository.findById(USER_ID_2)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(ITEM_ID_2)).thenReturn(Optional.of(availableItem));

        assertThrows(BadRequestException.class,
                () -> bookingService.saveRequest(inValidBookingRequest, USER_ID_2));
    }

    @Test
    void approve_WhenBookingNotFound_ThrowsException() {
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.approved(USER_ID, BOOKING_ID, true));
    }

    @Test
    void approve_ByNonOwner_ThrowsValidationException() {
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(waitingBooking));

        assertThrows(BadRequestException.class,
                () -> bookingService.approved(2L, BOOKING_ID, true));
    }

    @Test
    void approve_AlreadyApproved_ThrowsValidationException() {
        Booking approvedBooking = createBooking(
                BOOKING_ID, availableItem, owner, StatusType.APPROVED, NOW.minusDays(2), NOW.plusDays(2)
        );

        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(approvedBooking));

        assertThrows(BadRequestException.class,
                () -> bookingService.approved(USER_ID, BOOKING_ID, true));
    }

    @Test
    void approve_WithApprovalTrue_UpdatesStatusToApproved() {
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(waitingBooking));
        when(bookingRepository.save(waitingBooking)).thenReturn(waitingBooking);

        BookingResponse response = bookingService.approved(USER_ID, BOOKING_ID, true);

        assertEquals(StatusType.APPROVED, response.getStatus());
    }

    @Test
    void approve_WithApprovalFalse_UpdatesStatusToApproved() {
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(waitingBooking));
        when(bookingRepository.save(waitingBooking)).thenReturn(waitingBooking);

        BookingResponse response = bookingService.approved(USER_ID, BOOKING_ID, false);

        assertEquals(StatusType.REJECTED, response.getStatus());
    }

    @Test
    void testFindById_WithNonExistUserAndBooking_ThrowsNotFoundException() {
        assertThrows(
                NotFoundException.class,
                () -> bookingService.findById(100L, 100L)
        );
    }

    @Test
    void testFindById_WithExistUserAndBooking_ThrowsNotFoundException() {
        when(bookingRepository.findById(USER_ID)).thenReturn(Optional.of(waitingBooking));

        BookingResponse bookingResponse = bookingService.findById(USER_ID, BOOKING_ID);
        assertEquals(BOOKING_ID, bookingResponse.getId());
    }

    @Test
    void testFindByIdNotBooking_ByIdUserNotOwner_ThrowsBadRequestException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(waitingBooking));

        assertThrows(
                BadRequestException.class,
                () -> bookingService.findById(2L, BOOKING_ID)
        );
    }

    @Test
    void testFindAllByUserId_WrongStatus_ThrowsBadRequestException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(owner));

        assertThrows(
                BadRequestException.class,
                () -> bookingService.findAllByUserId(1L, "WrongStatus")
        );
    }

    @Test
    void testFindAllByUserId_NotUser_ThrowsBadRequestException() {
        assertThrows(
                NotFoundException.class,
                () -> bookingService.findAllByUserId(100L, "ALL")
        );
    }

    @Test
    void testFindAllByUserIdAll_StatusAll_ReturnsAllSavedBooking() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByBookerId(USER_ID, DEFAULT_SORT))
                .thenReturn(List.of(waitingBooking, canceledBooking));

        List<BookingResponse> bookingResponses = bookingService.findAllByUserId(USER_ID, "ALL");

        assertEquals(2, bookingResponses.size());
    }

    @Test
    void testFindAllByUserIdAll_StatusCurrent_ReturnsAllSavedBooking() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(
                eq(USER_ID),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                eq(Sort.by(Sort.Direction.DESC, "start"))
        )).thenReturn(List.of(waitingBooking));

        List<BookingResponse> bookingResponses = bookingService.findAllByUserId(USER_ID, "CURRENT");

        assertEquals(1, bookingResponses.size());
    }

    @Test
    void testFindAllByUserIdAll_StatusPast_ReturnsAllSavedBooking() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByBookerIdAndEndBefore(
                eq(USER_ID),
                any(LocalDateTime.class),
                eq(Sort.by(Sort.Direction.DESC, "start"))
        )).thenReturn(List.of(waitingBooking));

        List<BookingResponse> bookingResponses = bookingService.findAllByUserId(USER_ID, "PAST");

        assertEquals(1, bookingResponses.size());
    }

    @Test
    void testFindAllByUserIdAll_StatusFuture_ReturnsAllSavedBooking() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByBookerIdAndStartAfter(
                eq(USER_ID),
                any(LocalDateTime.class),
                eq(Sort.by(Sort.Direction.DESC, "start"))
        )).thenReturn(List.of(waitingBooking));

        List<BookingResponse> bookingResponses = bookingService.findAllByUserId(USER_ID, "FUTURE");

        assertEquals(1, bookingResponses.size());
    }

    @Test
    void testFindAllByUserIdAll_StatusWaiting_ReturnsAllSavedBooking() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByBookerIdAndStatusIs(
                eq(USER_ID),
                eq(StatusType.WAITING),
                eq(Sort.by(Sort.Direction.DESC, "start"))
        )).thenReturn(List.of(waitingBooking));

        List<BookingResponse> bookingResponses = bookingService.findAllByUserId(USER_ID, "WAITING");

        assertEquals(1, bookingResponses.size());
    }

    @Test
    void testFindAllByUserIdAll_StatusRejected_ReturnsAllSavedBooking() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByBookerIdAndStatusIs(
                eq(USER_ID),
                eq(StatusType.REJECTED),
                eq(Sort.by(Sort.Direction.DESC, "start"))
        )).thenReturn(List.of(waitingBooking));

        List<BookingResponse> bookingResponses = bookingService.findAllByUserId(USER_ID, "REJECTED");

        assertEquals(1, bookingResponses.size());
    }

    @Test
    void testFindAllByUserIdAll_StatusWaiting_ReturnsAllWaitingSavedBooking() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByBookerIdAndStatusIs(USER_ID, StatusType.WAITING, DEFAULT_SORT))
                .thenReturn(List.of(waitingBooking));


        List<BookingResponse> bookingResponses = bookingService.findAllByUserId(USER_ID, "WAITING");

        assertEquals(1, bookingResponses.size());
    }

    @Test
    void findAllByOwnerId_WrongStatus_ThrowsBadRequestException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(owner));

        assertThrows(
                BadRequestException.class,
                () -> bookingService.findAllByOwnerId(1L, "WrongStatus")
        );
    }

    @Test
    void findAllByOwnerId_NotUser_ThrowsNotFoundException() {
        assertThrows(
                NotFoundException.class,
                () -> bookingService.findAllByOwnerId(100L, "ALL")
        );
    }

    @Test
    void findAllByOwnerId_StatusWaiting_ReturnsAllWaiting() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(USER_ID)).thenReturn(List.of(availableItem));
        when(bookingRepository.findAllByItemOwnerIdAndStatusIs(USER_ID, StatusType.WAITING, DEFAULT_SORT))
                .thenReturn(List.of(waitingBooking));

        List<BookingResponse> bookingResponses = bookingService.findAllByOwnerId(USER_ID, "WAITING");

        assertEquals(1, bookingResponses.size());
    }

    @Test
    void findAllByOwnerId_StatusRejected_ReturnsAllRejected() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(USER_ID)).thenReturn(List.of(availableItem));
        when(bookingRepository.findAllByItemOwnerIdAndStatusIs(USER_ID, StatusType.REJECTED, DEFAULT_SORT))
                .thenReturn(List.of(waitingBooking));

        List<BookingResponse> bookingResponses = bookingService.findAllByOwnerId(USER_ID, "REJECTED");

        assertEquals(1, bookingResponses.size());
    }

    @Test
    void findAllByOwnerId_StatusFuture_ReturnsAllSavedBooking() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(USER_ID)).thenReturn(List.of(availableItem));
        when(bookingRepository.findAllByItemOwnerIdAndStartAfter(eq(USER_ID),
                any(LocalDateTime.class),
                eq(Sort.by(Sort.Direction.DESC, "start"))))
                .thenReturn(List.of(waitingBooking));

        List<BookingResponse> bookingResponses = bookingService.findAllByOwnerId(USER_ID, "FUTURE");

        assertEquals(1, bookingResponses.size());
    }

    @Test
    void findAllByOwnerId_StatusPast_ReturnsAllSavedBooking() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(USER_ID)).thenReturn(List.of(availableItem));
        when(bookingRepository.findAllByItemOwnerIdAndEndBefore(eq(USER_ID),
                any(LocalDateTime.class),
                eq(Sort.by(Sort.Direction.DESC, "start"))))
                .thenReturn(List.of(waitingBooking));

        List<BookingResponse> bookingResponses = bookingService.findAllByOwnerId(USER_ID, "PAST");

        assertEquals(1, bookingResponses.size());
    }


    private User createUser(String name, String email) {
        User user = new User();
        user.setId(USER_ID);
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private Item createItem(Long id, String name, String description, User owner, boolean available) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setOwner(owner);
        item.setAvailable(available);
        return item;
    }

    private BookingRequest createBookingRequest(Long itemId, LocalDateTime start, LocalDateTime end) {
        BookingRequest request = new BookingRequest();
        request.setItemId(itemId);
        request.setStart(start);
        request.setEnd(end);
        return request;
    }

    private Booking createBooking(Long id, Item item, User booker, StatusType status,
                                  LocalDateTime start, LocalDateTime end) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);
        booking.setStart(start);
        booking.setEnd(end);
        return booking;
    }
}