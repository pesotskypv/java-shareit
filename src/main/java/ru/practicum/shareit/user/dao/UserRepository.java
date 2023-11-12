package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User createUser(User user);

    Optional<User> getUser(Long id);

    List<User> findAllUsers();

    User updateUser(User user);

    void deleteUser(Long id);
}
