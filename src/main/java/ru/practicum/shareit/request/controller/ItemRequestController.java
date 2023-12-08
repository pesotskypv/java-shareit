package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOwn;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                     @RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        log.info("Получен POST-запрос /requests с телом={} и userId={}", itemRequestDto, userId);

        return itemRequestService.addRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDtoOwn> findItemRequestsByUser(
            @RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        log.info("Получен GET-запрос /requests с userId={}", userId);

        return itemRequestService.findItemRequestsByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoOwn> findItemRequestsByAnotherUser(
            @RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId,
            @RequestParam(value = "from", defaultValue = "0", required = false) @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10", required = false) @Positive Integer size) {
        log.info("Получен GET-запрос /requests/all?from={}&size={} с userId={}", from, size, userId);

        return itemRequestService.findItemRequestsByAnotherUser(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoOwn getItemRequestById(@PathVariable @Positive Long requestId,
            @RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        log.info("Получен GET-запрос /requests/{} с userId={}", requestId, userId);

        return itemRequestService.getItemRequestById(userId, requestId);
    }
}
