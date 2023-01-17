package ru.yandex.practicum.stats.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            IllegalArgumentException.class
    })
    ResponseEntity<ApiError> handleBadRequestExceptions(final Exception e) {
        String exceptionName = e.getClass().getName();
        String exceptionMessage = e.getMessage();
        exceptionName = exceptionName.substring(exceptionName.lastIndexOf(".") + 1);

        if (e instanceof MethodArgumentNotValidException) {
            int start = exceptionMessage.lastIndexOf("[") + 1;
            exceptionMessage = e.getMessage().substring(start, exceptionMessage.indexOf("]", start));
        }

        log.debug(e.getMessage());
        return new ResponseEntity<>(
                new ApiError(
                        List.of(Arrays.toString(e.getStackTrace())),
                        exceptionMessage,
                        exceptionName,
                        HttpStatus.BAD_REQUEST.toString(),
                        LocalDateTime.now()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @Getter
    @AllArgsConstructor
    static class ApiError {

        private List<String> errors;
        private String message;
        private String reason;
        private String status;
        private LocalDateTime time;
    }
}

