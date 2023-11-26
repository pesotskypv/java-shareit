package ru.practicum.shareit.item.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class InMemoryItemRepository implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private Long idCounter = 0L;

    @Override
    public Item save(Item item) {
        final Long newId;

        if (item.getId() == null) {
            newId = ++idCounter;
            item.setId(newId);
        }
        items.put(item.getId(), item);
        log.info("Вещь item={} сохранена в InMemoryItemRepository", item);

        return item;
    }

    @Override
    public Optional<Item> findById(Long id) {
        Optional<Item> item = Optional.ofNullable(items.get(id));
        log.info("Вещь item={} получена из InMemoryItemRepository", item);

        return item;
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<Item> findAll() {
        List<Item> foundItems = Collections.emptyList();

        if (!items.isEmpty()) {
            foundItems = new ArrayList<>(items.values());
        }
        log.info("Вещи items={} получены из InMemoryItemRepository", items);

        return foundItems;
    }

    @Override
    public List<Item> findItemsByOwnerId(Long id) {
        List<Item> items = this.items.values().stream()
                .filter(item -> item.getOwner().getId().equals(id))
                .collect(Collectors.toList());
        log.info("Вещи items={} получены из InMemoryItemRepository", items);

        return items;
    }

    @Override
    public List<Item> findItemsByText(String text) {
        List<Item> items = this.items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(item -> item.getAvailable().equals(true)).collect(Collectors.toList());
        log.info("Вещи items={} получены из InMemoryItemRepository", items);

        return items;
    }

    @Override
    public List<Item> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Item> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<Item> findAllById(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(Item entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Item> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public <S extends Item> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Item> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Item> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Item> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Item getOne(Long aLong) {
        return null;
    }

    @Override
    public Item getById(Long aLong) {
        return null;
    }

    @Override
    public Item getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends Item> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Item> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Item> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Item> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Item> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Item> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Item, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R>
            queryFunction) {
        return null;
    }
}
