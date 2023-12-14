package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOwn;
import ru.practicum.shareit.item.dto.ItemDtoReq;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDtoReq itemDtoReq) {
        log.info("Получен POST-запрос /items с телом={} и userId={}", itemDtoReq, userId);

        return itemService.createItem(userId, itemDtoReq);
    }

    @GetMapping("/{itemId}")
    public ItemDtoOwn getItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Получен GET-запрос /items/{} с userId={}", itemId, userId);

        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public List<ItemDtoOwn> findItemsByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        log.info("Получен GET-запрос /items?from={}&size={} с userId={}", from, size, userId);

        List<ItemDtoOwn> itemsDtoOwner = itemService.findItemsByUser(userId);

        return itemsDtoOwner.subList(from, Math.min((from + size), itemsDtoOwner.size()));
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam String text,
            @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        log.info("Получен GET-запрос /items/search?text={}&from={}&size={} с userId={}", text, from, size, userId);

        List<ItemDto> itemsDto = itemService.findItemsByText(userId, text);

        return itemsDto.subList(from, Math.min((from + size), itemsDto.size()));
    }

    @PatchMapping("/{itemId}")
    public ItemDto editItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                            @RequestBody ItemDto itemDto) {
        log.info("Получен PATCH-запрос /items/{} с телом={} и userId={}", itemId, itemDto, userId);

        return itemService.updateItem(userId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                                 @RequestBody CommentDto commentDto) {
        log.info("Получен POST-запрос /items/{}/comment с телом={} и userId={}", itemId, commentDto, userId);

        return itemService.addComment(userId, itemId, commentDto);
    }
}
