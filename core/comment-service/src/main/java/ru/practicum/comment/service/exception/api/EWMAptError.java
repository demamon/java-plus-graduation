package ru.practicum.comment.service.exception.api;

import java.util.List;

public record EWMAptError(
        List<String> errors,
        String message,
        String reason,
        int status,
        String timestamp
) {

}
