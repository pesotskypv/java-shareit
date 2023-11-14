package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@Valid @RequestBody ItemDto itemDto,
                           @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен POST-запрос /items с телом={} от пользователя={}", itemDto, userId);

        return itemService.createItem(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId, @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен GET-запрос /items/{} от пользователя={}", itemId, userId);

        return itemService.getItem(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> findItemsByUser(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен GET-запрос /items от пользователя={}", userId);

        return itemService.findItemsByUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam @NotBlank String text,
                                     @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен GET-запрос /items/search?text={}", text);

        return itemService.findItemsByText(text, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto editItem(@PathVariable Long itemId, @RequestBody ItemDto itemDto,
                              @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен PATCH-запрос /items/{} с телом={} от пользователя={}", itemId, itemDto, userId);

        return itemService.updateItem(itemId, itemDto, userId);
    }
}
