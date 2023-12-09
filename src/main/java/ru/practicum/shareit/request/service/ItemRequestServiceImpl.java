package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoOwn;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemRequestDto addRequest(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Попытка добавить новый запрос вещи несуществующим " +
                        "пользователем"));

        return itemRequestMapper.toItemRequestDto(itemRequestRepository.save(ItemRequest.builder()
                .description(itemRequestDto.getDescription()).requestor(user).created(LocalDateTime.now()).build()));
    }

    @Override
    public List<ItemRequestDtoOwn> findItemRequestsByUser(Long userId) {
        if(!userRepository.existsById(userId))
            throw new EntityNotFoundException("Попытка получить список запросов вещей несуществующим пользователем");

        return itemRequestRepository.findRequestsByRequestorId(userId).stream().map(ir ->
                itemRequestMapper.toItemRequestDtoOwn(ir, itemRepository.findItemsByRequestId(ir.getId()).stream()
                        .map(itemMapper::toItemDtoItemRequestOwn).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDtoOwn> findItemRequestsByAnotherUser(Long userId, Integer offset, Integer limit) {
        if(!userRepository.existsById(userId))
            throw new EntityNotFoundException("Попытка получить список запросов вещей несуществующим пользователем");

        return itemRequestRepository
                .findAll(PageRequest.of(offset, limit, Sort.by("created").descending())).stream()
                .filter(ir -> !ir.getRequestor().getId().equals(userId)).map(ir ->
                        itemRequestMapper.toItemRequestDtoOwn(ir, itemRepository.findItemsByRequestId(ir.getId())
                                .stream().map(itemMapper::toItemDtoItemRequestOwn).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDtoOwn getItemRequestById(Long userId, Long requestId) {
        if(!userRepository.existsById(userId))
            throw new EntityNotFoundException("Попытка получить список запросов вещей несуществующим пользователем");

        return itemRequestMapper.toItemRequestDtoOwn(itemRequestRepository.findById(requestId)
                        .orElseThrow(() -> new EntityNotFoundException("Попытка получить запрос несуществующей вещи")),
                itemRepository.findItemsByRequestId(requestId).stream().map(itemMapper::toItemDtoItemRequestOwn)
                        .collect(Collectors.toList()));
    }
}
