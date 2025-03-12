package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper mapper;
    private final CommentMapper commentMapper;

    public ItemServiceImpl(ItemRepository itemRepository, CommentRepository commentRepository, BookingRepository bookingRepository,
                           UserRepository userRepository, ItemMapper mapper, CommentMapper commentMapper) {
        this.itemRepository = itemRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.mapper = mapper;
        this.commentMapper = commentMapper;
    }

    /**
     * Добавить вещь в репозиторий.
     *
     * @param userId  ID владельца вещи.
     * @param itemDto добавленная вещь.
     * @return добавленная вещь.
     */
    @Override
    @Transactional
    public ItemDto add(long userId, ItemDto itemDto) {
        User owner = getUserByIdOrException(userId);
        itemDto.setOwner(owner);
        Item item = mapper.toItem(itemDto);
        return mapper.toItemDto(itemRepository.save(item));
    }

    /**
     * Обновить вещь в репозитории.
     *
     * @param userId  ID владельца вещи.
     * @param itemId  ID вещи.
     * @param itemDto добавленная вещь.
     * @return добавленная вещь.
     */
    @Override
    @Transactional
    public ItemDto update(long userId, Long itemId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id - [" + itemId + "]"));
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("Только владелец может редактировать данные о вещи." +
                    "Пользователь с id = [" + userId + "] не владелец вещи с id - [" + itemId + "]");
        }
        if (Objects.nonNull(itemDto.getName())) {
            item.setName(itemDto.getName());
        }
        if ((Objects.nonNull(itemDto.getDescription()))) {
            item.setDescription(itemDto.getDescription());
        }
        if ((Objects.nonNull(itemDto.getAvailable()))) {
            item.setAvailable(itemDto.getAvailable());
        }
        Item updateItem = itemRepository.save(item);
        return mapper.toItemDto(updateItem);
    }

    /**
     * Найти по ID вещи
     *
     * @param itemId
     * @return
     */
    @Override
    public ItemResponce findById(long ownerId, long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id - [" + itemId + "]"));
        List<CommentDto> commentsDto = commentRepository.findAllByItemId(itemId).stream()
                .map(comment -> commentMapper.toCommentDto(comment)).collect(Collectors.toList());
        ItemResponce itemResponce = mapper.toItemResponce(item, commentsDto);
        if (item.getOwner().getId().equals(ownerId)) {
            Optional<Booking> last = bookingRepository.findTopByItemIdAndEndBeforeAndStatusInOrderByEndDesc(itemId,
                    LocalDateTime.now(), List.of(StatusType.APPROVED));
            itemResponce.setLastBooking(last.map(Booking::getEnd).orElse(null));
            Optional<Booking> future = bookingRepository.findTopByItemIdAndStartAfterAndStatusInOrderByStartAsc(itemId,
                    LocalDateTime.now(), List.of(StatusType.APPROVED));
            itemResponce.setNextBooking(future.map(Booking::getStart).orElse(null));
        }
        return itemResponce;
    }

    /**
     * Найти по ID пользователя вещей
     *
     * @param userId
     * @return
     */
    @Override
    public List<ItemDto> findAllByUserId(long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            itemDtos.add(mapper.toItemDto(item));
        }
        return itemDtos;
    }

    /**
     * Найти по text система ищет вещи, содержащие этот текст в названии или описании
     *
     * @param text
     * @return
     */
    @Override
    public List<ItemDto> findAllByText(String text) {
        if (text.trim().isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> items = itemRepository.search(text.trim().toLowerCase());
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            itemDtos.add(mapper.toItemDto(item));
        }
        return itemDtos;
    }


    @Override
    @Transactional
    public CommentDto saveComment(long userId, long itemId, CommentDto commentDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} не найден." + userId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещи с id = {} не найдена." + itemId));
        if (bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(userId, itemId,
                        StatusType.APPROVED, LocalDateTime.now())
                .isEmpty()) {
            throw new BadRequestException("Нельзя написать отзыв если пользователь не брал в аренду вещь");
        }
        Comment comment = commentMapper.toComment(commentDto, owner, item);
        commentRepository.save(comment);
        return commentMapper.toCommentDto(comment);
    }

    private User getUserByIdOrException(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователя с id - [{}]" + userId));
    }
}
