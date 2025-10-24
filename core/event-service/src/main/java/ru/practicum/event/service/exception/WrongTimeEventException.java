package ru.practicum.event.service.exception;

public class WrongTimeEventException extends RuntimeException {

    public WrongTimeEventException(String message) {
        super(message);
    }
}
