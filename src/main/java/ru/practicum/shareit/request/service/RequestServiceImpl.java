package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
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

@Slf4j
@Service
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;

    private final RequestMapper requestMapper;

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final ItemRepository itemRepository;

    private final ItemMapper itemMapper;
    private final Sort sort = Sort.by(Sort.Direction.DESC, "created");

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
    public RequestResponseDto save(Long userId, RequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} нет." + userId));
        Request request = requestRepository.save(requestMapper.toRequest(requestDto,
                user));
        RequestResponseDto requestResponseDto =
                requestMapper.toRequestResponseDto(request);
        requestResponseDto.setRequestor(userMapper.toUserDto(user));
        return requestResponseDto;
    }

    @Override
    public List<RequestResponseDto> getAllByUserId(Long userId) {
        List<Request> requests = requestRepository.findAllByRequestorId(userId, sort);
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
    public List<RequestResponseDto> findAllExceptUserId(Long userId) {
        final List<Request> requests = requestRepository.findAllExceptUserId(userId, sort);
        log.info("Получены все запросы кроме запросов пользователя с id = {}", userId);
        final List<RequestResponseDto> list = new ArrayList<>();
        for (Request request : requests) {
            final RequestResponseDto requestResponseDto =
                    requestMapper.toRequestResponseDto(request);
            requestResponseDto.setRequestor(userMapper.toUserDto(request.getRequestor()));
            list.add(requestResponseDto);
        }
        return list;
    }

    @Override
    public RequestResponseDto getByRequestId(Long requestId, Long userId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запроса с id = {} нет." + requestId));
        RequestResponseDto requestResponseDto =
                requestMapper.toRequestResponseDto(request);
        List<ItemDtoForRequest> items = itemRepository.findAllByRequest(request)
                .stream().map(itemMapper::toItemDtoForRequest).toList();
        requestResponseDto.setItems(items);
        requestResponseDto.setRequestor(userMapper.toUserDto(request.getRequestor()));
        return requestResponseDto;
    }
}
