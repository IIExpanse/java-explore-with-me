package ru.yandex.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewm.event.dto.*;
import ru.yandex.practicum.ewm.event.model.EventSortType;
import ru.yandex.practicum.ewm.event.model.EventState;
import ru.yandex.practicum.ewm.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@ResponseStatus(HttpStatus.OK)
public class EventController {

    private final EventService service;

    @PostMapping(path = "/users/{userId}/events")
    public EventFullDto addEvent(
            @RequestBody @Valid NewEventDto newEventDto,
            @PathVariable Long userId) {
        return service.addEvent(newEventDto, userId);
    }

    @GetMapping(path = "/events/{id}")
    public EventFullDto getEvent(
            @PathVariable Long id,
            @Autowired HttpServletRequest servletRequest) {
        return service.getEvent(id, servletRequest.getRemoteAddr());
    }

    @GetMapping(path = "/users/{userId}/events/{eventId}")
    public EventFullDto getEventByInitiator(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        return service.getEventByInitiator(userId, eventId);
    }

    @GetMapping(path = "/users/{userId}/events")
    public Collection<EventShortDto> getEventsByInitiator(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        return service.getEventsByInitiator(userId, from, size);
    }

    @GetMapping(path = "/events")
    public Collection<EventShortDto> getFilteredEventsPublic(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) LocalDateTime rangeStart,
            @RequestParam(required = false) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(defaultValue = "EVENT_DATE") String sort,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size,
            @Autowired HttpServletRequest servletRequest
    ) {

        return service.getFilteredEventsPublic(
                text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                EventSortType.valueOf(sort),
                from,
                size,
                servletRequest.getRemoteAddr()
        );
    }

    @GetMapping(path = "/admin/events")
    public Collection<EventFullDto> getFilteredEventsInternal(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) LocalDateTime rangeStart,
            @RequestParam(required = false) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size
    ) {

        return service.getFilteredEventsInternal(
                users,
                states,
                categories,
                rangeStart,
                rangeEnd,
                from,
                size
        );
    }

    @PatchMapping(path = "/users/{userId}/events")
    public EventFullDto updateEvent(
            @RequestBody @Valid UpdateEventRequest request,
            @PathVariable Long userId) {
        return service.updateEvent(request, userId);
    }

    @PutMapping(path = "/admin/events/{eventId}")
    public EventFullDto updateEventAdmin(
            @RequestBody @Valid AdminUpdateEventRequest request,
            @PathVariable Long eventId) {
        return service.updateEventAdmin(request, eventId);
    }

    @PatchMapping(path = "/users/{userId}/events/{eventId}")
    public EventFullDto cancelEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        return service.cancelEvent(eventId, userId);
    }

    @PatchMapping(path = "/admin/events/{eventId}/publish")
    public EventFullDto publishEvent(
            @PathVariable Long eventId) {
        return service.publishEvent(eventId);
    }

    @PatchMapping(path = "/admin/events/{eventId}/reject")
    public EventFullDto rejectEvent(
            @PathVariable Long eventId) {
        return service.rejectEvent(eventId);
    }
}
