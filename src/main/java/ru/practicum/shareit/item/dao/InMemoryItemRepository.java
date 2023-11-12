package ru.practicum.shareit.item.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class InMemoryItemRepository implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private Long idCounter = 0L;

    @Override
    public Item addItem(Item item) {
        final Long newId = ++idCounter;

        item.setId(newId);
        items.put(newId, item);
        log.info("Вещь item={} добавлена в InMemoryItemRepository", item);

        return item;
    }

    @Override
    public Optional<Item> getItem(Long id) {
        Optional<Item> item = Optional.ofNullable(items.get(id));
        log.info("Вещь item={} получена из InMemoryItemRepository", item);

        return item;
    }

    @Override
    public List<Item> findAllItems(Long id) {
        List<Item> foundItems = Collections.emptyList();

        if (!items.isEmpty()) {
            foundItems = new ArrayList<>(items.values());
        }
        log.info("Вещи items={} получены из InMemoryItemRepository", items);

        return foundItems;
    }

    @Override
    public List<Item> findItemsByUser(Long id) {
        List<Item> items = this.items.values().stream()
                .filter(item -> item.getOwner().getId().equals(id))
                .collect(Collectors.toList());
        log.info("Вещи items={} получены из InMemoryItemRepository", items);

        return items;
    }

    @Override
    public List<Item> findItemsByText(String text) {
        List<Item> items = Collections.emptyList();

        if (!text.isBlank()) {
            items = this.items.values().stream()
                    .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                            || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                    .filter(item -> item.getAvailable().equals(true))
                    .collect(Collectors.toList());
        }
        log.info("Вещи items={} получены из InMemoryItemRepository", items);

        return items;
    }

    @Override
    public Item updateItem(Item item) {
        Item updatedItem = items.get(item.getId());
        String name = item.getName();
        String description = item.getDescription();
        Boolean available = item.getAvailable();

        if (name != null) {
            updatedItem.setName(name);
        }
        if (description != null) {
            updatedItem.setDescription(description);
        }
        if (available != null) {
            updatedItem.setAvailable(available);
        }
        log.info("Вещь item={} обновлена из InMemoryItemRepository", item);

        return updatedItem;
    }
}
