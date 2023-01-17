package ru.yandex.practicum.ewm.event.service;

import ru.yandex.practicum.ewm.event.dto.*;
import ru.yandex.practicum.ewm.event.model.Event;
import ru.yandex.practicum.ewm.event.model.EventSortType;
import ru.yandex.practicum.ewm.event.model.EventState;

import java.time.LocalDateTime;
import java.util.Collection;

public interface EventService {

    EventFullDto addEvent(NewEventDto newEventDto, long userId);

    EventFullDto getEvent(long eventId, String ip);

    Event getEventModel(long eventId);

    EventFullDto getEventByInitiator(long userId, long eventId);

    Collection<EventShortDto> getEventsByInitiator(long userId, int from, int size);

    Collection<EventShortDto> getFilteredEventsPublic(
            String text,
            Collection<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            boolean onlyAvailable,
            EventSortType sortType,
            int from,
            int size,
            String ip
    );

    Collection<EventFullDto> getFilteredEventsInternal(
            Collection<Long> users,
            Collection<EventState> states,
            Collection<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from,
            int size
    );

    EventFullDto updateEvent(UpdateEventRequest request, long userId);

    EventFullDto updateEventAdmin(AdminUpdateEventRequest request, long eventId);

    EventFullDto cancelEvent(long eventId, long userId);

    EventFullDto publishEvent(long eventId);

    EventFullDto rejectEvent(long eventId);
}
