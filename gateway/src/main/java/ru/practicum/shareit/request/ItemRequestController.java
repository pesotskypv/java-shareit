package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                             @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен POST-запрос /requests с телом={} и userId={}", itemRequestDto, userId);

        return itemRequestClient.addRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findItemRequestsByUser(@RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Получен GET-запрос /requests с userId={}", userId);

        return itemRequestClient.findItemRequestsByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findItemRequestsByAnotherUser(
            @RequestHeader("X-Sharer-User-Id") @Positive long userId,
            @RequestParam(name = "from", defaultValue = "0", required = false) @PositiveOrZero Integer from,
            @RequestParam(name = "size", defaultValue = "10", required = false) @Positive Integer size) {
        log.info("Получен GET-запрос /requests/all?from={}&size={} с userId={}", from, size, userId);

        return itemRequestClient.findItemRequestsByAnotherUser(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable @Positive long requestId,
            @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Получен GET-запрос /requests/{} с userId={}", requestId, userId);

        return itemRequestClient.getItemRequestById(userId, requestId);
    }
}
