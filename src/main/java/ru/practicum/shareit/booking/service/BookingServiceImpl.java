package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.enums.StatusType;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingRequest;
import ru.practicum.shareit.booking.model.BookingResponce;
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
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;

    private final BookingMapper bookingMapper;

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final ItemRepository itemRepository;

    private final ItemMapper itemMapper;

    public BookingServiceImpl(BookingRepository bookingRepository, BookingMapper bookingMapper,
                              UserRepository userRepository, UserMapper userMapper,
                              ItemRepository itemRepository, ItemMapper itemMapper) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
    }

    Sort sort = Sort.by(Sort.Direction.DESC, "start");

    @Override
    @Transactional
    public BookingResponce saveRequest(BookingRequest bookingRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {" + userId + "} нет."));
        Item item = itemRepository.findById(bookingRequest.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещи с id = {" + bookingRequest.getItemId() + "} нет."));
        if (item.getOwner().getId().equals(userId)) {
            throw new BadRequestException("Нельзя забронировать вещь у самого себя");
        }
        if (!item.getAvailable()) {
            throw new BadRequestException("Вещь не доступна для бронирования.");
        }
        if (bookingRequest.getStart().isAfter(bookingRequest.getEnd())
                || bookingRequest.getStart().isEqual(bookingRequest.getEnd())
                || bookingRequest.getEnd().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Дата окончания бронирования не может быть раньше или равна дате начала");
        }
        Booking booking = bookingRepository.save(bookingMapper.toBooking(bookingRequest, user, item));
        log.info("Запрос на бронирование вещи с id {} успешно сохранен", bookingRequest.getItemId());
        return bookingMapper.toBookingResponce(booking, userMapper.toUserDto(user), itemMapper.toItemDto(item));
    }

    @Override
    @Transactional
    public BookingResponce approved(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Такого запроса на бронирование не было"));
        if (!ownerId.equals(booking.getItem().getOwner().getId())) {
            throw new BadRequestException("Подтвердить бронирование может только владелец вещи");
        }
        if (!booking.getStatus().equals(StatusType.WAITING)) {
            throw new BadRequestException("Нельзя подтверждать бронирование" +
                    " если оно не находится в ожидании подтвержления");
        }
        if (approved) {
            booking.setStatus(StatusType.APPROVED);
        } else {
            booking.setStatus(StatusType.REJECTED);
        }
        Booking updateBooking = bookingRepository.save(booking);
        log.info("Выполнен запрос на подтверждение бронирование вещи с id " + bookingId);
        return bookingMapper.toBookingResponce(updateBooking,
                userMapper.toUserDto(booking.getBooker()), itemMapper.toItemDto(booking.getItem()));
    }

    @Override
    public BookingResponce findById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Такого запроса на бронирование не было"));
        if (!booking.getBooker().getId().equals(userId)
                && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new BadRequestException("Посмотреть бронирование может только владелец вещи" +
                    "или человек, забронировавший ее");
        }
        return bookingMapper.toBookingResponce(booking,
                userMapper.toUserDto(booking.getBooker()), itemMapper.toItemDto(booking.getItem()));
    }

    @Override
    public List<BookingResponce> findAllByUserId(Long userId, String state) {
        userRepository.findById(userId);
        List<Booking> bookings = switch (state) {
            case "ALL" -> bookingRepository.findAllByBookerId(userId, sort);
            case "CURRENT" -> bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(userId,
                    LocalDateTime.now(), LocalDateTime.now(), sort);
            case "PAST" -> bookingRepository.findAllByBookerIdAndEndBefore(userId, LocalDateTime.now(), sort);
            case "FUTURE" -> bookingRepository.findAllByBookerIdAndStartAfter(userId, LocalDateTime.now(), sort);
            case "WAITING" -> bookingRepository.findAllByBookerIdAndStatusIs(userId, StatusType.WAITING.toString(), sort);
            case "REJECTED" -> bookingRepository.findAllByBookerIdAndStatusIs(userId, StatusType.REJECTED.toString(), sort);
            default -> throw new BadRequestException("Неверно передан параметр state");
        };
        return bookings.stream()
                .map(booking -> bookingMapper.toBookingResponce(booking,
                        userMapper.toUserDto(booking.getBooker()), itemMapper.toItemDto(booking.getItem())))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponce> findAllByOwnerId(Long ownerId, String state) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} нет." + ownerId));
        if (itemRepository.findAllByOwnerId(ownerId).isEmpty()) {
            throw new BadRequestException("у пользователя пока нет вещей");
        }
        List<Booking> bookings = switch (state) {
            case "ALL" -> bookingRepository.findAllByItemOwnerId(ownerId, sort);
            case "CURRENT" -> bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(ownerId,
                    LocalDateTime.now(), LocalDateTime.now(), sort);
            case "PAST" -> bookingRepository.findAllByItemOwnerIdAndEndBefore(ownerId, LocalDateTime.now(), sort);
            case "FUTURE" -> bookingRepository.findAllByItemOwnerIdAndStartAfter(ownerId, LocalDateTime.now(), sort);
            case "WAITING" -> bookingRepository.findAllByItemOwnerIdAndStatusIs(ownerId, StatusType.WAITING.toString(), sort);
            case "REJECTED" -> bookingRepository.findAllByItemOwnerIdAndStatusIs(ownerId, StatusType.REJECTED.toString(), sort);
            default -> throw new BadRequestException("Неверно передан параметр state");
        };
        return bookings.stream()
                .map(booking -> bookingMapper.toBookingResponce(booking,
                        userMapper.toUserDto(booking.getBooker()), itemMapper.toItemDto(booking.getItem())))
                .collect(Collectors.toList());
    }
}
