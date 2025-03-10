package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.enums.StatusType;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(Long userId, Sort sort);

    List<Booking> findAllByBookerIdAndEndBeforeAndStartAfter(Long userId, LocalDateTime time1,
                                                             LocalDateTime time2, Sort sort);

    List<Booking> findAllByBookerIdAndEndBefore(Long userId, LocalDateTime time, Sort sort);

    List<Booking> findAllByBookerIdAndStartAfter(Long userId, LocalDateTime time, Sort sort);

    List<Booking> findAllByBookerIdAndStatusIs(Long userId, String status, Sort sort);

    List<Booking> findAllByItemOwnerId(Long userId, Sort sort);

    List<Booking> findAllByItemOwnerIdAndEndBeforeAndStartAfter(Long userId, LocalDateTime time1,
                                                                LocalDateTime time2, Sort sort);

    List<Booking> findAllByItemOwnerIdAndEndBefore(Long userId, LocalDateTime time, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartAfter(Long userId, LocalDateTime time, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStatusIs(Long userId, String status, Sort sort);

    List<Booking> findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(Long userId,
                                                                          Long itemId, StatusType status,
                                                                          LocalDateTime time);

    Optional<Booking> findTopByItemIdAndEndBeforeAndStatusInOrderByEndDesc(Long itemId,
                                                                           LocalDateTime time, List<StatusType> status);

    Optional<Booking> findTopByItemIdAndStartAfterAndStatusInOrderByStartAsc(Long itemId,
                                                                             LocalDateTime time, List<StatusType> status);
}
