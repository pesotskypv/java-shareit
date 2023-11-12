package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserValidation;
import ru.practicum.shareit.user.dto.UpdateUserValidation;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Validated(value = {CreateUserValidation.class}) @RequestBody UserDto userDto) {
        log.info("Получен POST-запрос /users с телом={}", userDto);

        return userService.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        log.info("Получен GET-запрос /users/{}", userId);

        return userService.getUser(userId);
    }

    @GetMapping
    public List<UserDto> findAllUsers() {
        log.info("Получен GET-запрос /users");

        return userService.findAllUsers();
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId,
                              @Validated(value = {UpdateUserValidation.class}) @RequestBody UserDto userDto) {
        log.info("Получен PATCH-запрос /users/{} с телом={}", userId, userDto);

        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Получен DELETE-запрос /users/{}", userId);
        userService.deleteUser(userId);
    }
}
