package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private Long idCounter = 0L;

    /**
     * @param user 
     * @return
     */
    @Override
    public User createUser(User user) {
        final Long newId = ++idCounter;

        user.setId(newId);
        users.put(newId, user);
        log.info("Пользователь user={} создан в InMemoryUserRepository", user);

        return user;
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Optional<User> getUser(Long id) {
        Optional<User> user = Optional.ofNullable(users.get(id));
        log.info("Пользователь user={} получен из InMemoryUserRepository", user);

        return user;
    }

    /**
     * @return 
     */
    @Override
    public List<User> findAllUsers() {
        List<User> foundUsers = Collections.emptyList();

        if (!users.isEmpty()) {
            foundUsers = new ArrayList<>(users.values());
        }
        log.info("Пользователи users={} получены из InMemoryUserRepository", foundUsers);

        return foundUsers;
    }

    /**
     * @param user
     * @return
     */
    @Override
    public User updateUser(User user) {
        User updatedUser = users.get(user.getId());
        String name = user.getName();
        String email = user.getEmail();

        if (name != null) {
            updatedUser.setName(name);
        }
        if (email != null) {
            updatedUser.setEmail(email);
        }
        log.info("Пользователь user={} обновлён в InMemoryUserRepository", user);

        return updatedUser;
    }

    /**
     * @param id
     */
    @Override
    public void deleteUser(Long id) {
        users.remove(id);
        log.info("Пользователь userId={} удалён из InMemoryUserRepository", id);
    }
}
