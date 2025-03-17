package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestResponseDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RequestServiceImpl implements RequestService {
    private static final Sort SORT_BY_CREATED_DESC = Sort.by(Sort.Direction.DESC, "created");

    private final RequestRepository requestRepository;

    private final RequestMapper requestMapper;

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final ItemRepository itemRepository;

    private final ItemMapper itemMapper;

    public RequestServiceImpl(RequestRepository itemRequestRepository, RequestMapper itemRequestMapper,
                              UserRepository userRepository, UserMapper userMapper,
                              ItemRepository itemRepository, ItemMapper itemMapper) {
        this.requestRepository = itemRequestRepository;
        this.requestMapper = itemRequestMapper;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
    }

    @Override
    @Transactional
    public RequestResponseDto save(Long userId, RequestDto requestDto) {
        User user = getUserByIdOrThrow(userId);
        Request request = requestRepository.save(requestMapper.toRequest(requestDto, user));
        RequestResponseDto requestResponseDto = requestMapper.toRequestResponseDto(request);
        requestResponseDto.setRequestor(userMapper.toUserDto(user));
        return requestResponseDto;
    }

    @Override
    public List<RequestResponseDto> getAllByUserId(Long userId) {
        checkUserExists(userId);
        List<Request> requests = requestRepository.findAllByRequestorId(userId, SORT_BY_CREATED_DESC);
        List<RequestResponseDto> list = new ArrayList<>();
        for (Request request : requests) {
            RequestResponseDto requestResponseDto =
                    requestMapper.toRequestResponseDto(request);
            List<ItemDtoForRequest> items = itemRepository.findAllByRequest(request)
                    .stream().map(itemMapper::toItemDtoForRequest).toList();
            requestResponseDto.setItems(items);
            requestResponseDto.setRequestor(userMapper.toUserDto(request.getRequestor()));
            list.add(requestResponseDto);
        }
        return list;
    }

    @Override
    public List<RequestResponseDto> findAllExceptUserId(Long userId, int from, int size) {
        checkUserExists(userId);
        PageRequest page = PageRequest.of(from / size, size, SORT_BY_CREATED_DESC);

        List<Request> requests = requestRepository.findAllByRequestorIdNot(userId, page);
        return requests.stream()
                .map(request -> {
                    RequestResponseDto requestResponseDto = requestMapper.toRequestResponseDto(request);
                    requestResponseDto.setRequestor(userMapper.toUserDto(request.getRequestor()));
                    return requestResponseDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public RequestResponseDto getByRequestId(Long requestId) {
        Request request = getRequestByIdOrThrow(requestId);

        RequestResponseDto requestResponseDto =
                requestMapper.toRequestResponseDto(request);
        List<ItemDtoForRequest> items = itemRepository.findAllByRequest(request)
                .stream().map(itemMapper::toItemDtoForRequest).toList();
        requestResponseDto.setItems(items);
        requestResponseDto.setRequestor(userMapper.toUserDto(request.getRequestor()));
        return requestResponseDto;
    }

    private User getUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=%d не найден".formatted(userId)));
    }

    private Request getRequestByIdOrThrow(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id=%d не найден".formatted(requestId)));
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id=%d не найден".formatted(userId));
        }
    }
}
