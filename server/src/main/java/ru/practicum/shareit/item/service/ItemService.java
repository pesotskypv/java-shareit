package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOwn;
import ru.practicum.shareit.item.dto.ItemDtoReq;

import java.util.List;

public interface ItemService {

    ItemDto createItem(Long userId, ItemDtoReq itemDtoReq);

    ItemDtoOwn getItem(Long userId, Long itemId);

    List<ItemDtoOwn> findItemsByUser(Long userId);

    List<ItemDto> findItemsByText(Long userId, String text);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
