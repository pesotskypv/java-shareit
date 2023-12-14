package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOwn;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен POST-запрос /requests с телом={} и userId={}", itemRequestDto, userId);

        return itemRequestService.addRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDtoOwn> findItemRequestsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен GET-запрос /requests с userId={}", userId);

        return itemRequestService.findItemRequestsByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoOwn> findItemRequestsByAnotherUser(@RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        log.info("Получен GET-запрос /requests/all?from={}&size={} с userId={}", from, size, userId);

        return itemRequestService.findItemRequestsByAnotherUser(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoOwn getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PathVariable Long requestId) {
        log.info("Получен GET-запрос /requests/{} с userId={}", requestId, userId);

        return itemRequestService.getItemRequestById(userId, requestId);
    }
}
