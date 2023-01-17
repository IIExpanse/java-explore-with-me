package ru.yandex.practicum.ewm.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.ewm.category.exception.CantDeleteUsedCategoryException;
import ru.yandex.practicum.ewm.category.exception.CategoryNameAlreadyExistsException;
import ru.yandex.practicum.ewm.category.exception.CategoryNotFoundException;
import ru.yandex.practicum.ewm.compilation.exception.CompilationNotFoundException;
import ru.yandex.practicum.ewm.compilation.exception.PinnedAlreadySetException;
import ru.yandex.practicum.ewm.event.exception.*;
import ru.yandex.practicum.ewm.request.exception.*;
import ru.yandex.practicum.ewm.user.exception.DuplicateEmailException;
import ru.yandex.practicum.ewm.user.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({
            CanOnlyCancelPendingEventException.class,
            CanOnlyPublishPendingEventsException.class,
            CantEditPublishedEventException.class,
            CantRejectPublishedEventException.class,
            EventStartIsTooCloseException.class,
            CantParticipateInUnpublishedEventException.class,
            DuplicateParticipationRequestException.class,
            EventReachedMaxParticipantsException.class,
            ParticipationRequestInOwnEventException.class,
            RequestAlreadyApprovedException.class,
            RequestAlreadyRejectedException.class,
            RequestConfirmationNotRequiredException.class,
            PinnedAlreadySetException.class,
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

    @ExceptionHandler({
            WrongUserUpdatingEventException.class,
            WrongUserQueryingEventRequestsException.class,
            WrongUserUpdatingRequestException.class,
            WrongUserRequestingOwnerEventException.class
    })
    ResponseEntity<ApiError> handleForbiddenExceptions(final Exception e) {
        String exceptionName = e.getClass().getName();
        String exceptionMessage = e.getMessage();
        exceptionName = exceptionName.substring(exceptionName.lastIndexOf(".") + 1);

        log.debug(e.getMessage());
        return new ResponseEntity<>(
                new ApiError(
                        List.of(Arrays.toString(e.getStackTrace())),
                        exceptionMessage,
                        exceptionName,
                        HttpStatus.FORBIDDEN.toString(),
                        LocalDateTime.now()
                ),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler({
            UserNotFoundException.class,
            EventNotFoundException.class,
            CategoryNotFoundException.class,
            RequestNotFoundException.class,
            CompilationNotFoundException.class
    })
    ResponseEntity<ApiError> handleNotFoundExceptions(final Exception e) {
        String exceptionName = e.getClass().getName();
        String exceptionMessage = e.getMessage();
        exceptionName = exceptionName.substring(exceptionName.lastIndexOf(".") + 1);

        log.debug(e.getMessage());
        return new ResponseEntity<>(
                new ApiError(
                        List.of(Arrays.toString(e.getStackTrace())),
                        exceptionMessage,
                        exceptionName,
                        HttpStatus.NOT_FOUND.toString(),
                        LocalDateTime.now()
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler({
            DuplicateEmailException.class,
            CantDeleteUsedCategoryException.class,
            CategoryNameAlreadyExistsException.class
    })
    ResponseEntity<ApiError> handleConflictExceptions(final Exception e) {
        String exceptionName = e.getClass().getName();
        String exceptionMessage = e.getMessage();
        exceptionName = exceptionName.substring(exceptionName.lastIndexOf(".") + 1);

        log.debug(e.getMessage());
        return new ResponseEntity<>(
                new ApiError(
                        List.of(Arrays.toString(e.getStackTrace())),
                        exceptionMessage,
                        exceptionName,
                        HttpStatus.CONFLICT.toString(),
                        LocalDateTime.now()
                ),
                HttpStatus.CONFLICT
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

