package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item addItem(Item item);

    Optional<Item> getItem(Long id);

    List<Item> findAllItems(Long id);

    List<Item> findItemsByUser(Long id);

    List<Item> findItemsByText(String text);

    Item updateItem(Item item);
}
