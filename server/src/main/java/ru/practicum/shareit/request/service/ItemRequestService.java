package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOwn;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDtoOwn> findItemRequestsByUser(Long userId);

    List<ItemRequestDtoOwn> findItemRequestsByAnotherUser(Long userId, Integer from, Integer size);

    ItemRequestDtoOwn getItemRequestById(Long userId, Long requestId);
}
