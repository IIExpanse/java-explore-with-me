package ru.yandex.practicum.ewm.event.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.category.exception.CategoryNotFoundException;
import ru.yandex.practicum.ewm.category.model.Category;
import ru.yandex.practicum.ewm.category.repository.CategoryRepository;
import ru.yandex.practicum.ewm.event.dto.*;
import ru.yandex.practicum.ewm.event.exception.*;
import ru.yandex.practicum.ewm.event.mapper.EventMapper;
import ru.yandex.practicum.ewm.event.model.Event;
import ru.yandex.practicum.ewm.event.model.EventSortType;
import ru.yandex.practicum.ewm.event.model.EventState;
import ru.yandex.practicum.ewm.event.repository.EventRepository;
import ru.yandex.practicum.ewm.event.service.EventService;
import ru.yandex.practicum.ewm.request.service.RequestService;
import ru.yandex.practicum.ewm.stats.service.EwmStatsService;
import ru.yandex.practicum.ewm.user.exception.UserNotFoundException;
import ru.yandex.practicum.ewm.user.model.User;
import ru.yandex.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RequestService requestService;
    private final EwmStatsService statsService;
    private final EventMapper mapper;

    @Override
    public EventFullDto addEvent(NewEventDto newEventDto, long userId) {
        String context = "добавление события";
        Event event = mapper.mapToNewModel(
                newEventDto,
                getCategoryOrThrow(newEventDto.getCategory(), context),
                getUserOrThrow(userId, "добавление события"),
                LocalDateTime.now(),
                EventState.PENDING);

        if (event.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        }
        event = eventRepository.save(event);

        log.debug("Сохранено новое событие: {}", event);
        return mapper.mapToFullDto(event, 0, 0, new LocationDto(event));
    }

    @Override
    public EventFullDto getEvent(long eventId, String ip) {
        Event event = getEventModel(eventId);
        statsService.saveHit(eventId, ip);

        log.trace("Запрошено событие с id={}", eventId);
        return mapper.mapToFullDto(
                event,
                requestService.getConfirmedRequestsCount(eventId),
                statsService.getViewsForEvent(eventId),
                new LocationDto(event));
    }

    @Override
    public Event getEventModel(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(
                String.format("Ошибка при получении события: событие с id=%d не найдено.", eventId)));
    }

    @Override
    public EventFullDto getEventByInitiator(long userId, long eventId) {
        getUserOrThrow(userId, "получение события по инициатору");
        Event event = getEventModel(eventId);

        if (userId != event.getInitiator().getId()) {
            throw new WrongUserRequestingOwnerEventException(
                    String.format("Ошибка при получении события с id=%d по владельцу с id=%d: " +
                            "пользователь не является инициатором события.", eventId, userId));
        }

        log.trace("Запрошено событие с id={}", eventId);
        return mapper.mapToFullDto(
                event,
                requestService.getConfirmedRequestsCount(eventId),
                statsService.getViewsForEvent(eventId),
                new LocationDto(event));
    }

    @Override
    public Collection<EventShortDto> getEventsByInitiator(long userId, int from, int size) {
        Collection<Event> events = eventRepository.findAllByInitiatorId(userId, Pageable.ofSize(size)).stream()
                .skip(from)
                .collect(Collectors.toSet());
        Collection<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toSet());

        Map<Long, Integer> requestsCountMap = requestService.getConfirmedRequestsForCollection(eventIds);
        Map<Long, Integer> viewsCountMap = statsService.getViewsForCollection(eventIds);

        log.trace("Запрошена подборка событий инициатора с id={}", userId);
        return events.stream()
                .skip(from)
                .map(event -> mapper.mapToShortDto(
                        event,
                        requestsCountMap.get(event.getId()),
                        viewsCountMap.get(event.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<EventShortDto> getFilteredEventsPublic(
            String text,
            Collection<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            boolean onlyAvailable,
            EventSortType sortType,
            int from,
            int size,
            String ip) {

        Collection<Event> events = eventRepository.getFilteredEventsPublic(
                text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                size);
        Collection<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toSet());

        Map<Long, Integer> requestsCountMap = requestService.getConfirmedRequestsForCollection(eventIds);
        Map<Long, Integer> viewsCountMap = statsService.getViewsForCollection(eventIds);

        Stream<EventShortDto> shortDtoStream = events.stream()
                .map(event -> mapper.mapToShortDto(
                        event,
                        requestsCountMap.get(event.getId()),
                        viewsCountMap.get(event.getId())));

        Collection<EventShortDto> shortDtos;

        if (sortType == EventSortType.VIEWS) {
            shortDtoStream = shortDtoStream.sorted(Comparator.comparingInt(EventShortDto::getViews).reversed());
        }
        shortDtos = shortDtoStream.skip(from).collect(Collectors.toList());
        statsService.saveHit(null, ip);

        log.trace("Запрошена подборка событий с публичного эндпоинта.");
        return shortDtos;
    }

    @Override
    public Collection<EventFullDto> getFilteredEventsInternal(
            Collection<Long> users,
            Collection<EventState> states,
            Collection<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from,
            int size) {

        Collection<Event> events = eventRepository.getFilteredEventsInternal(
                        users,
                        states,
                        categories,
                        rangeStart,
                        rangeEnd,
                        size).stream()
                .skip(from)
                .collect(Collectors.toSet());

        Collection<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toSet());

        Map<Long, Integer> requestsCountMap = requestService.getConfirmedRequestsForCollection(eventIds);
        Map<Long, Integer> viewsCountMap = statsService.getViewsForCollection(eventIds);

        log.trace("Запрошена подборка событий со внутреннего эндпоинта.");
        return events.stream()
                .map(event -> mapper.mapToFullDto(
                        event,
                        requestsCountMap.get(event.getId()),
                        viewsCountMap.get(event.getId()),
                        new LocationDto(event.getLat(), event.getLon())))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEvent(UpdateEventRequest request, long userId) {
        getUserOrThrow(userId, "обновление события");
        Event event = getEventModel(request.getEventId());
        long eventId = event.getId();
        EventState state = event.getState();
        Category category;

        if (userId != event.getInitiator().getId()) {
            throw new WrongUserUpdatingEventException(String.format("Ошибка при обновлении события с id=%d: " +
                    "запрос исходит от пользователя, не являющегося владельцем", eventId));

        } else if (state == EventState.PUBLISHED) {
            throw new CantEditPublishedEventException(String.format("Ошибка при обновлении события с id=%d: " +
                    "нельзя отредактировать опубликованное событие.", eventId));

        } else if (state == EventState.CANCELED) {
            event.setState(EventState.PENDING);
        }

        if (request.getCategory() == null) {
            category = event.getCategory();

        } else {
            category = getCategoryOrThrow(request.getCategory(), "обновление события");
        }

        event = mapper.updateEvent(event, request, category);

        log.debug("Обновлено событие с id={}", eventId);
        return mapper.mapToFullDto(eventRepository.save(event),
                requestService.getConfirmedRequestsCount(eventId),
                statsService.getViewsForEvent(eventId),
                new LocationDto(event));
    }

    @Override
    public EventFullDto updateEventAdmin(AdminUpdateEventRequest request, long eventId) {
        Event event = getEventModel(eventId);
        String context = "обновление события администратором";
        Category category;

        if (request.getCategory() == null) {
            category = event.getCategory();

        } else {
            category = getCategoryOrThrow(request.getCategory(), context);
        }
        event = mapper.updateEventAdmin(event, request, category);

        log.debug("Обновлено событие с id={}", eventId);
        return mapper.mapToFullDto(eventRepository.save(event),
                requestService.getConfirmedRequestsCount(eventId),
                statsService.getViewsForEvent(eventId),
                new LocationDto(event));
    }

    @Override
    public EventFullDto cancelEvent(long eventId, long userId) {
        getUserOrThrow(userId, "отмена события");
        Event event = getEventModel(eventId);

        if (userId != event.getInitiator().getId()) {
            throw new WrongUserUpdatingEventException(String.format("Ошибка при отмене события с id=%d: " +
                    "запрос исходит от пользователя, не являющегося владельцем.", eventId));

        } else if (event.getState() != EventState.PENDING) {
            throw new CanOnlyCancelPendingEventException(String.format("Ошибка при отмене события с id=%d: " +
                    "можно отменить только событие в состоянии ожидания модерации.", eventId));
        }
        event.setState(EventState.CANCELED);

        log.debug("Отменено событие с id={}", eventId);
        return mapper.mapToFullDto(eventRepository.save(event),
                requestService.getConfirmedRequestsCount(eventId),
                statsService.getViewsForEvent(eventId),
                new LocationDto(event));
    }

    @Override
    public EventFullDto publishEvent(long eventId) {
        Event event = getEventModel(eventId);

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new EventStartIsTooCloseException(String.format("Ошибка при публикации события с id=%d: " +
                    "время начала события должно быть не раньше, чем через час от текущего момента.", eventId));

        } else if (event.getState() != EventState.PENDING) {
            throw new CanOnlyPublishPendingEventsException(String.format("Ошибка при публикации события с id=%d: " +
                    "можно опубликовать только события, ожидающие публикации.", eventId));
        }
        event.setState(EventState.PUBLISHED);
        event.setPublishedOn(LocalDateTime.now());

        return mapper.mapToFullDto(event,
                requestService.getConfirmedRequestsCount(eventId),
                statsService.getViewsForEvent(eventId),
                new LocationDto(event));
    }

    @Override
    public EventFullDto rejectEvent(long eventId) {
        Event event = getEventModel(eventId);

        if (event.getState() == EventState.PUBLISHED) {
            throw new CantRejectPublishedEventException(String.format("Ошибка при отклонении события с id=%d: " +
                    "нельзя отклонить опубликованное событие.", eventId));
        }
        event.setState(EventState.CANCELED);

        return mapper.mapToFullDto(event,
                requestService.getConfirmedRequestsCount(eventId),
                statsService.getViewsForEvent(eventId),
                new LocationDto(event));
    }

    private User getUserOrThrow(long userId, String context) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
                String.format("Ошибка при операции '%s': пользователь с id=%d не найден.", context, userId)));
    }

    private Category getCategoryOrThrow(Long catId, String context) {
        return categoryRepository.findById(catId).orElseThrow(() -> new CategoryNotFoundException(
                String.format("Ошибка при операции %s: категория не найдена.", context)));
    }
}
