package ru.practicum.comment.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.practicum.comment.service.exception.api.EWMAptError;

import java.time.LocalDateTime;
import java.util.List;

@ControllerAdvice
public class EWMExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<EWMAptError> handleNotFoundException(final NotFoundException ex) {
        final EWMAptError error = new EWMAptError(
                List.of(ex.getStackTrace()[0].toString()),
                ex.getMessage(),
                "Element Not Found Error",
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now().toString()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<EWMAptError> handleMethodArgumentNotValidException(final MethodArgumentNotValidException ex) {
        final EWMAptError error = new EWMAptError(
                List.of(ex.getStackTrace()[0].toString()),
                ex.getMessage(),
                "Validation Error",
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now().toString()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
}
