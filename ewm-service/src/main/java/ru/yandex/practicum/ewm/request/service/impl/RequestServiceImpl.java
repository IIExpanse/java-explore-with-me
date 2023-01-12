package ru.yandex.practicum.ewm.request.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.event.exception.EventNotFoundException;
import ru.yandex.practicum.ewm.event.model.Event;
import ru.yandex.practicum.ewm.event.model.EventState;
import ru.yandex.practicum.ewm.event.repository.EventRepository;
import ru.yandex.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.yandex.practicum.ewm.request.exception.*;
import ru.yandex.practicum.ewm.request.mapper.RequestMapper;
import ru.yandex.practicum.ewm.request.model.ParticipationRequest;
import ru.yandex.practicum.ewm.request.model.RequestStatus;
import ru.yandex.practicum.ewm.request.repository.RequestRepository;
import ru.yandex.practicum.ewm.request.service.RequestService;
import ru.yandex.practicum.ewm.user.exception.UserNotFoundException;
import ru.yandex.practicum.ewm.user.model.User;
import ru.yandex.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final UserRepository userRepository;

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final RequestMapper mapper;

    @Override
    @Transactional
    public ParticipationRequestDto addRequest(long userId, long eventId) {
        String context = "добавление запроса на участие в событии";
        User user = getUserOrThrow(userId, context);
        Event event = getEventOrThrow(eventId, context);
        RequestStatus status;

        Supplier<String> errorString = () -> String.format("Ошибка при добавлении запроса на участие в событии " +
                "с id=%d пользователем с id=%d: ", eventId, userId);

        if (requestRepository.existsByRequesterId(userId)) {
            throw new DuplicateParticipationRequestException(errorString.get() +
                    "запрос на участие уже существует.");

        } else if (event.getInitiator().getId() == userId) {
            throw new ParticipationRequestInOwnEventException(errorString.get() +
                    "пользователь является владельцем события.");

        } else if (event.getState() != EventState.PUBLISHED) {
            throw new CantParticipateInUnpublishedEventException(errorString.get() +
                    "нельзя добавить запрос на участие в неопубликованном событии.");

        } else if (event.getParticipantLimit() != 0
                && getConfirmedRequestsCount(eventId) == event.getParticipantLimit()) {
            throw new EventReachedMaxParticipantsException(errorString.get() +
                    "событие достигло максимального количества запросов на участие.");
        }

        if (!event.getRequestModeration()) {
            status = RequestStatus.CONFIRMED;

        } else {
            status = RequestStatus.PENDING;
        }

        ParticipationRequest request = mapper.mapToNewModel(
                LocalDateTime.now(),
                event,
                user,
                status
        );
        request = requestRepository.save(request);

        log.debug("Добавлен новый запрос на участие в событии: {}", request);
        return mapper.mapToDto(request);
    }

    @Override
    public Collection<ParticipationRequestDto> getRequestsByUser(long userId) {
        getUserOrThrow(userId, "получение запросов на участие, поданных конкретным пользователем");

        log.trace("Получение запросов пользователя с id={}", userId);
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(mapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ParticipationRequestDto> getRequestsByEventOwner(long ownerId, long eventId) {
        String context = "получение запросов на участие в конкретном событии";
        getUserOrThrow(ownerId, context);
        Event event = getEventOrThrow(eventId, context);

        if (ownerId != event.getInitiator().getId()) {
            throw new WrongUserQueryingEventRequestsException(
                    String.format("Ошибка при получении запросов на участие " +
                            "в событии с id=%d пользователем с id=%d: " +
                            "пользователь не является инициатором события.", eventId, ownerId));
        }

        log.trace("Получение запросов на участие в событии с id={} пользователя с id={}", eventId, ownerId);
        return requestRepository.findAllByEventInitiatorIdAndEventId(ownerId, eventId).stream()
                .map(mapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public int getConfirmedRequestsCount(long eventId) {
        return requestRepository.countAllByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        String context = "отмена запроса на участие в событии";
        getUserOrThrow(userId, context);
        ParticipationRequest request = getRequestModel(requestId, context);
        request.setStatus(RequestStatus.CANCELED);
        request = requestRepository.save(request);

        log.debug("Отменен запрос на участие с id={}", requestId);
        return mapper.mapToDto(request);
    }

    @Override
    @Transactional
    public ParticipationRequestDto confirmRequestInOwnerEvent(long ownerId, long eventId, long requestId) {
        String context = "подтверждение запроса на участие в событии";
        getUserOrThrow(ownerId, context);
        Event event = getEventOrThrow(eventId, context);
        ParticipationRequest request;

        Supplier<String> errorString = () -> String.format("Ошибка при подтверждении запроса с id=%d " +
                "на участие в событии с id=%d пользователем с id=%d: ", requestId, eventId, ownerId);

        if (ownerId != event.getInitiator().getId()) {
            throw new WrongUserUpdatingRequestException(errorString.get() +
                    "пользователь не является инициатором события.");

        } else if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new RequestConfirmationNotRequiredException(errorString.get() +
                    "подтверждения запросов на участие в указанном событии не требуется.");

        } else if (getConfirmedRequestsCount(eventId) == event.getParticipantLimit()) {
            requestRepository.rejectPendingRequestsInFullEvent(eventId);
            throw new EventReachedMaxParticipantsException(errorString.get() +
                    "событие достигло максимального количества запросов на участие.");
        }
        request = getRequestModel(requestId, context);

        if (request.getStatus() == RequestStatus.CONFIRMED) {
            throw new RequestAlreadyApprovedException(errorString.get() + "запрос уже одобрен.");
        }
        request.setStatus(RequestStatus.CONFIRMED);
        request = requestRepository.save(requestRepository.save(request));

        log.debug("Одобрен запрос с id={}", requestId);
        return mapper.mapToDto(request);
    }

    @Override
    @Transactional
    public ParticipationRequestDto rejectRequestInOwnerEvent(long ownerId, long eventId, long requestId) {
        String context = "подтверждение запроса на участие в событии";
        getUserOrThrow(ownerId, context);
        Event event = getEventOrThrow(eventId, context);
        ParticipationRequest request;

        Supplier<String> errorString = () -> String.format("Ошибка при подтверждении запроса с id=%d " +
                "на участие в событии с id=%d пользователем с id=%d: ", requestId, eventId, ownerId);

        if (ownerId != event.getInitiator().getId()) {
            throw new WrongUserUpdatingRequestException(errorString.get() +
                    "пользователь не является инициатором события.");
        }
        request = getRequestModel(requestId, context);

        if (request.getStatus() == RequestStatus.REJECTED) {
            throw new RequestAlreadyRejectedException(errorString.get() + "запрос уже отклонен.");
        }
        request.setStatus(RequestStatus.REJECTED);
        request = requestRepository.save(request);

        log.debug("Отклонен запрос с id={}", eventId);
        return mapper.mapToDto(request);
    }

    private ParticipationRequest getRequestModel(long requestId, String context) {
        Optional<ParticipationRequest> requestOptional = requestRepository.findById(requestId);

        if (requestOptional.isEmpty()) {
            throw new RequestNotFoundException(
                    String.format("Ошибка при операции '%s': запрос с id=%d не найден.", context, requestId));
        }
        return requestOptional.get();
    }

    private User getUserOrThrow(long userId, String context) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(
                    String.format("Ошибка при операции '%s': пользователь с id=%d не найден.",
                            context,
                            userId));
        }
        return userOptional.get();
    }

    private Event getEventOrThrow(long eventId, String context) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);

        if (eventOptional.isEmpty()) {
            throw new EventNotFoundException(
                    String.format("Ошибка при операции '%s': событие с id=%d не найдено.",
                            context,
                            eventId));
        }
        return eventOptional.get();
    }
}
