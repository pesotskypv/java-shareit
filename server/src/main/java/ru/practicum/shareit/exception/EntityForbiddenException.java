package ru.practicum.shareit.exception;

public class EntityForbiddenException extends RuntimeException {

    public EntityForbiddenException(String message) {
        super(message);
    }
}
