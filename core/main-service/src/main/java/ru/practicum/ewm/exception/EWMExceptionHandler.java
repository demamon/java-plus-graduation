package ru.practicum.ewm.exception;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.practicum.ewm.exception.api.EWMAptError;

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

    @ExceptionHandler({
            ConflictException.class,
            DuplicatedEmailException.class
    })
    public ResponseEntity<EWMAptError> handleConflict(final RuntimeException ex) {
        final EWMAptError error = new EWMAptError(
                List.of(ex.getStackTrace()[0].toString()),
                ex.getMessage(),
                "Conflict Error",
                HttpStatus.CONFLICT.value(),
                LocalDateTime.now().toString()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler({
            ValidationException.class,
            WrongTimeEventException.class
    })
    public ResponseEntity<EWMAptError> handleBadRequest(final RuntimeException ex) {
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
