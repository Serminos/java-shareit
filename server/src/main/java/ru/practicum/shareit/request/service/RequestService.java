package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestResponseDto;

import java.util.List;

public interface RequestService {
    RequestResponseDto save(Long userId, RequestDto requestDto);

    List<RequestResponseDto> getAllByUserId(Long userId);

    List<RequestResponseDto> findAllExceptUserId(Long userId);

    RequestResponseDto getByRequestId(Long requestId, Long userId);
}
