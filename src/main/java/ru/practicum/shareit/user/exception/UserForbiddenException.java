package ru.practicum.shareit.user.exception;

public class UserForbiddenException extends RuntimeException {

    public UserForbiddenException(String message) {
        super(message);
    }
}
