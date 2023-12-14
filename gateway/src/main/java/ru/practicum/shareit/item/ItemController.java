package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoReq;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                          @Valid @RequestBody ItemDtoReq itemDtoReq) {
        log.info("Получен POST-запрос /items с телом={} и userId={}", itemDtoReq, userId);

        return itemClient.createItem(userId, itemDtoReq);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                          @PathVariable @Positive long itemId) {
        log.info("Получен GET-запрос /items/{} с userId={}", itemId, userId);

        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findItemsByUser(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Получен GET-запрос /items?from={}&size={} с userId={}", from, size, userId);

        return itemClient.findItemsByUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
            @RequestParam String text,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Получен GET-запрос /items/search?text={}&from={}&size={} с userId={}", text, from, size, userId);

        return itemClient.findItemsByText(userId, text, from, size);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> editItem(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                           @PathVariable @Positive long itemId,
                                           @RequestBody ItemDto itemDto) {
        log.info("Получен PATCH-запрос /items/{} с телом={} и userId={}", itemId, itemDto, userId);

        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                             @PathVariable @Positive long itemId,
                                             @Valid @RequestBody CommentDto commentDto) {
        log.info("Получен POST-запрос /items/{}/comment с телом={} и userId={}", itemId, commentDto, userId);

        return itemClient.addComment(userId, itemId, commentDto);
    }
}
