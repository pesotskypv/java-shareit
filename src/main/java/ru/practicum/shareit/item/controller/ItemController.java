package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOwn;
import ru.practicum.shareit.item.dto.ItemDtoReq;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@Valid @RequestBody ItemDtoReq itemDtoReq,
                           @RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        log.info("Получен POST-запрос /items с телом={} и userId={}", itemDtoReq, userId);

        return itemService.createItem(userId, itemDtoReq);
    }

    @GetMapping("/{itemId}")
    public ItemDtoOwn getItem(@PathVariable @Positive Long itemId,
                              @RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        log.info("Получен GET-запрос /items/{} с userId={}", itemId, userId);

        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public List<ItemDtoOwn> findItemsByUser(
            @RequestParam(value = "from", defaultValue = "0", required = false) @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10", required = false) @Positive Integer size,
            @RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        log.info("Получен GET-запрос /items?from={}&size={} с userId={}", from, size, userId);

        List<ItemDtoOwn> itemsDtoOwner = itemService.findItemsByUser(userId);

        return itemsDtoOwner.subList(from, Math.min((from + size), itemsDtoOwner.size()));
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(
            @RequestParam String text,
            @RequestParam(value = "from", defaultValue = "0", required = false) @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10", required = false) @Positive Integer size,
            @RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        log.info("Получен GET-запрос /items/search?text={}&from={}&size={} с userId={}", text, from, size, userId);

        List<ItemDto> itemsDto = itemService.findItemsByText(userId, text);

        return itemsDto.subList(from, Math.min((from + size), itemsDto.size()));
    }

    @PatchMapping("/{itemId}")
    public ItemDto editItem(@PathVariable @Positive Long itemId, @RequestBody ItemDto itemDto,
                            @RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        log.info("Получен PATCH-запрос /items/{} с телом={} и userId={}", itemId, itemDto, userId);

        return itemService.updateItem(userId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable @Positive Long itemId, @Valid @RequestBody CommentDto commentDto,
                                 @RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        log.info("Получен POST-запрос /items/{}/comment с телом={} и userId={}", itemId, commentDto, userId);

        return itemService.addComment(userId, itemId, commentDto);
    }
}
