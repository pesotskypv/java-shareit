package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto getItem(Long itemId, Long userId);

    List<ItemDto> findItemsByUser(Long userId);

    List<ItemDto> findItemsByText(String text, Long userId);

    ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId);
}
