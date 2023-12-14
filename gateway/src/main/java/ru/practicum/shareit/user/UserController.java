package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserValidation;
import ru.practicum.shareit.user.dto.UpdateUserValidation;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Positive;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(
            @Validated(value = {CreateUserValidation.class}) @RequestBody UserDto userDto) {
        log.info("Получен POST-запрос /users с телом={}", userDto);

        return userClient.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable @Positive long userId) {
        log.info("Получен GET-запрос /users/{}", userId);

        return userClient.getUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllUsers() {
        log.info("Получен GET-запрос /users");

        return userClient.findAllUsers();
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable @Positive long userId,
                              @Validated(value = {UpdateUserValidation.class}) @RequestBody UserDto userDto) {
        log.info("Получен PATCH-запрос /users/{} с телом={}", userId, userDto);

        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable @Positive long userId) {
        log.info("Получен DELETE-запрос /users/{}", userId);
        userClient.deleteUser(userId);
    }
}
