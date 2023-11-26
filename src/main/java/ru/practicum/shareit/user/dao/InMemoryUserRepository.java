package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EntityValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.function.Function;

@Slf4j
@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private Long idCounter = 0L;

    @Override
    public User save(User user) {
        final Long newId;

        checkFreeEmail(user);
        if (user.getId() == null) {
            newId = ++idCounter;
            user.setId(newId);
        }
        users.put(user.getId(), user);
        log.info("Пользователь user={} сохранён в InMemoryUserRepository", user);

        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        Optional<User> user = Optional.ofNullable(users.get(id));
        log.info("Пользователь user={} получен из InMemoryUserRepository", user);

        return user;
    }

    @Override
    public List<User> findAll() {
        List<User> foundUsers = Collections.emptyList();

        if (!users.isEmpty()) {
            foundUsers = new ArrayList<>(users.values());
        }
        log.info("Пользователи users={} получены из InMemoryUserRepository", foundUsers);

        return foundUsers;
    }

    @Override
    public void deleteById(Long id) {
        users.remove(id);
        log.info("Пользователь userId={} удалён из InMemoryUserRepository", id);
    }

    private void checkFreeEmail(User user) {
        Long id = user.getId();
        String email = user.getEmail();

        if (findAll().stream().filter(u -> !u.getId().equals(id))
                .anyMatch(u -> u.getEmail().equals(email))) {
            String textError = "Уже существует пользователь c электронной почтой: " + email;

            log.debug("Валидация не пройдена: " + textError);
            throw new EntityValidationException(textError);
        }
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<User> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<User> findAllById(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public <S extends User> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends User> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends User> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<User> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public User getOne(Long aLong) {
        return null;
    }

    @Override
    public User getById(Long aLong) {
        return null;
    }

    @Override
    public User getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends User> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends User> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends User> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends User> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends User> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends User> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends User, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public void delete(User entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends User> entities) {

    }

    @Override
    public void deleteAll() {

    }
}
