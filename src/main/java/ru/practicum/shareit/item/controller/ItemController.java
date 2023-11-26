package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOwner;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@Valid @RequestBody ItemDto itemDto,
                           @RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        log.info("Получен POST-запрос /items с телом={} и userId={}", itemDto, userId);

        return itemService.createItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDtoOwner getItem(@PathVariable @Positive Long itemId,
                                @RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        log.info("Получен GET-запрос /items/{} с userId={}", itemId, userId);

        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public List<ItemDtoOwner> findItemsByUser(@RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        log.info("Получен GET-запрос /items с userId={}", userId);

        return itemService.findItemsByUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam @NotBlank String text,
                                     @RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        log.info("Получен GET-запрос /items/search?text={} с userId={}", text, userId);

        return itemService.findItemsByText(userId, text);
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
