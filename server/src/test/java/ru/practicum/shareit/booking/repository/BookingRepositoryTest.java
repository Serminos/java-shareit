package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.enums.StatusType;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    static final Sort SORT = Sort.by(Sort.Direction.DESC, "start");
    User owner;
    User booker;
    Item item1;
    Item item2;
    LocalDateTime now;

    @BeforeEach
    void setup() {
        now = LocalDateTime.now();

        owner = userRepository.save(createUser("Owner", "owner@example.com"));
        booker = userRepository.save(createUser("Booker", "booker@example.com"));

        item1 = itemRepository.save(createItem("Item 1", "Description 1", owner));
        item2 = itemRepository.save(createItem("Item 2", "Description 2", owner));
    }

    @Test
    void save_shouldSaveBooking() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        Booking savedBooking = createAndSaveBooking(item1, booker, StatusType.WAITING, start, end);

        assertNotNull(savedBooking.getId());
        assertEquals(StatusType.WAITING, savedBooking.getStatus());
        assertEquals(start, savedBooking.getStart());
        assertEquals(end, savedBooking.getEnd());
        assertEquals(booker.getId(), savedBooking.getBooker().getId());
        assertEquals(item1.getId(), savedBooking.getItem().getId());
    }

    @Test
    void findAll_WhenNoBookings_ShouldReturnEmptyList() {
        List<Booking> bookings = bookingRepository.findAll();

        assertTrue(bookings.isEmpty());
    }

    @Test
    void findAllByBookerId_ShouldReturnBookersBookings() {
        Booking booking = createAndSaveBooking(item1, booker, StatusType.APPROVED, now.plusDays(1), now.plusDays(2));

        List<Booking> result = bookingRepository.findAllByBookerId(booker.getId(), SORT);

        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void findAllCurrentByBooker_ShouldReturnCurrentBookings() {
        createAndSaveBooking(item1, booker, StatusType.APPROVED, now.minusDays(1), now.plusDays(1));

        List<Booking> result = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(
                booker.getId(), now, now, SORT
        );

        assertEquals(1, result.size());
        assertEquals(item1.getName(), result.get(0).getItem().getName());
    }

    @Test
    void findAllPastByBooker_ShouldReturnPastBookings() {
        createAndSaveBooking(item1, booker, StatusType.APPROVED, now.minusDays(2), now.minusDays(1));
        createAndSaveBooking(item2, booker, StatusType.APPROVED, now.plusDays(1), now.plusDays(2));

        List<Booking> result = bookingRepository.findAllByBookerIdAndEndBefore(
                booker.getId(), now, SORT
        );

        assertEquals(1, result.size());
        assertEquals(item1.getName(), result.get(0).getItem().getName());
    }

    @Test
    public void testFindAllByBookerIdAndStartAfter() {
        createAndSaveBooking(item1, booker, StatusType.APPROVED, now.minusDays(10), now.minusDays(5));
        createAndSaveBooking(item2, booker, StatusType.WAITING, now.plusDays(2), now.plusDays(10));


        List<Booking> result = bookingRepository.findAllByBookerIdAndStartAfter(booker.getId(),
                LocalDateTime.now(), SORT);

        assertEquals(1, result.size());
        assertEquals(item2.getName(), result.get(0).getItem().getName());
    }

    @Test
    void findTopByItemIdAndStartAfterAndStatusInOrderByStartAsc_ShouldReturnEarliestFutureBooking() {
        Booking expected = createAndSaveBooking(item1, booker, StatusType.APPROVED, now.plusDays(1), now.plusDays(2));
        createAndSaveBooking(item1, booker, StatusType.APPROVED, now.plusDays(3), now.plusDays(4));

        Optional<Booking> result = bookingRepository.findTopByItemIdAndStartAfterAndStatusInOrderByStartAsc(
                item1.getId(), now, List.of(StatusType.APPROVED)
        );

        assertTrue(result.isPresent());
        assertEquals(expected.getId(), result.get().getId());
    }

    @Test
    void findAllByBookerIdAndStatus_ShouldReturnFilteredBookings() {
        createAndSaveBooking(item1, booker, StatusType.APPROVED, now.plusDays(1), now.plusDays(2));
        createAndSaveBooking(item2, booker, StatusType.REJECTED, now.plusDays(3), now.plusDays(4));

        List<Booking> approved = bookingRepository.findAllByBookerIdAndStatusIs(
                booker.getId(), StatusType.APPROVED, SORT
        );

        List<Booking> rejected = bookingRepository.findAllByBookerIdAndStatusIs(
                booker.getId(), StatusType.REJECTED, SORT
        );

        assertAll(
                () -> assertEquals(1, approved.size(), "Approved bookings должно быть 1"),
                () -> assertEquals(StatusType.APPROVED, approved.get(0).getStatus()),
                () -> assertEquals(1, rejected.size(), "Rejected bookings должно быть 1"),
                () -> assertEquals(StatusType.REJECTED, rejected.get(0).getStatus())
        );
    }

    @Test
    void findAllByBookerId_WhenBookerNotExists_ShouldReturnEmpty() {
        List<Booking> result = bookingRepository.findAllByBookerId(999L, SORT);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllByItemOwnerId_ShouldReturnFilteredBookingsByOwnerId() {
        createAndSaveBooking(item1, booker, StatusType.WAITING, now.plusDays(1), now.plusDays(2));

        List<Booking> result = bookingRepository.findAllByItemOwnerId(
                owner.getId(), SORT
        );

        assertEquals(1, result.size());
        assertEquals(StatusType.WAITING, result.get(0).getStatus());
    }

    @Test
    void findAllByItemOwnerIdAndEndBeforeAndStartAfter_ShouldReturnFilteredBookings() {
        createAndSaveBooking(item1, booker, StatusType.APPROVED, now.minusDays(1), now.plusDays(2));
        createAndSaveBooking(item2, booker, StatusType.WAITING, now.plusDays(1), now.plusDays(2));

        List<Booking> result = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(
                owner.getId(), LocalDateTime.now(), LocalDateTime.now(), SORT
        );

        assertEquals(1, result.size());
        assertEquals(StatusType.APPROVED, result.get(0).getStatus());
    }

    @Test
    void findAllByItemOwnerIdAndEndBefore_ShouldReturnFilteredBookings() {
        createAndSaveBooking(item1, booker, StatusType.APPROVED, now.minusDays(10), now.minusDays(2));
        createAndSaveBooking(item2, booker, StatusType.WAITING, now.plusDays(1), now.plusDays(2));

        List<Booking> result = bookingRepository.findAllByItemOwnerIdAndEndBefore(
                owner.getId(), LocalDateTime.now(), SORT
        );

        assertEquals(1, result.size());
        assertEquals(StatusType.APPROVED, result.get(0).getStatus());
    }

    @Test
    void findAllByItemOwnerIdAndStartAfter_ShouldReturnFilteredBookings() {
        createAndSaveBooking(item1, booker, StatusType.APPROVED, now.minusDays(10), now.minusDays(2));
        createAndSaveBooking(item2, booker, StatusType.WAITING, now.plusDays(1), now.plusDays(2));

        List<Booking> result = bookingRepository.findAllByItemOwnerIdAndStartAfter(
                owner.getId(), LocalDateTime.now(), SORT
        );

        assertEquals(1, result.size());
        assertEquals(StatusType.WAITING, result.get(0).getStatus());
    }

    @Test
    void findAllByOwnerAndStatus_ShouldReturnFilteredBookings() {
        createAndSaveBooking(item1, booker, StatusType.WAITING, now.plusDays(1), now.plusDays(2));
        createAndSaveBooking(item2, booker, StatusType.APPROVED, now.plusDays(3), now.plusDays(4));

        List<Booking> result = bookingRepository.findAllByItemOwnerIdAndStatusIs(
                owner.getId(), StatusType.WAITING, SORT
        );

        assertEquals(1, result.size());
        assertEquals(StatusType.WAITING, result.get(0).getStatus());
    }

    @Test
    void findAllByBookerId_ShouldReturnSortedByStartDesc() {
        Booking first = createAndSaveBooking(item1, booker, StatusType.APPROVED, now.plusDays(5), now.plusDays(6));
        Booking second = createAndSaveBooking(item2, booker, StatusType.APPROVED, now.plusDays(3), now.plusDays(4));

        List<Booking> result = bookingRepository.findAllByBookerId(booker.getId(), SORT);

        assertAll(
                () -> assertEquals(2, result.size(), "Total bookings count"),
                () -> assertEquals(first.getId(), result.get(0).getId(), "First item"),
                () -> assertEquals(second.getId(), result.get(1).getId(), "Second item")
        );
    }

    @Test
    void findAllByStatus_WhenNoMatches_ShouldReturnEmpty() {
        createAndSaveBooking(item1, booker, StatusType.APPROVED, now.plusDays(1), now.plusDays(2));

        List<Booking> result = bookingRepository.findAllByBookerIdAndStatusIs(
                booker.getId(), StatusType.CANCELED, SORT
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void saveBookingWithNullStatus_ShouldError() {
        Booking booking = createBooking(item1, booker, null, now.plusDays(1), now.plusDays(2));

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            bookingRepository.save(booking);
        }, "Должно быть выброшено DataIntegrityViolationException при сохранении Booking с null статусом");

        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("NULL not allowed for column \"STATUS\""));

    }

    @Test
    void createOverlappingBooking_ShouldSaveWithoutValidation() {
        createAndSaveBooking(item1, booker, StatusType.APPROVED, now.plusDays(1), now.plusDays(3));

        // Попытка создать пересекающееся бронирование
        Booking overlapping = createAndSaveBooking(
                item1, booker, StatusType.WAITING, now.plusDays(2), now.plusDays(4)
        );

        assertNotNull(overlapping.getId());
    }

    @Test
    void findAllByBookerIdAndItemId_ShouldReturnRelatedBookings() {
        createAndSaveBooking(item1, booker, StatusType.APPROVED, now.minusDays(5), now.minusDays(3));
        createAndSaveBooking(item1, booker, StatusType.APPROVED, now.minusDays(2), now.minusDays(1));

        List<Booking> result = bookingRepository.findAllByBookerIdAndItemId(
                booker.getId(), item1.getId()
        );

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(b -> b.getItem().getId().equals(item1.getId())));
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private Item createItem(String name, String description, User owner) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(true);
        item.setOwner(owner);
        return item;
    }

    private Booking createAndSaveBooking(Item item, User booker, StatusType status,
                                         LocalDateTime start, LocalDateTime end) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);
        booking.setStart(start);
        booking.setEnd(end);
        return bookingRepository.save(booking);
    }

    private Booking createBooking(Item item, User booker, StatusType status,
                                  LocalDateTime start, LocalDateTime end) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);
        booking.setStart(start);
        booking.setEnd(end);
        return booking;
    }
}