package ru.yandex.practicum.ewm.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.yandex.practicum.ewm.request.service.RequestService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@ResponseStatus(HttpStatus.OK)
public class RequestController {

    private final RequestService service;

    @PostMapping(path = "/users/{userId}/requests")
    public ParticipationRequestDto addRequest(
            @PathVariable Long userId,
            @RequestParam Long eventId) {
        return service.addRequest(userId, eventId);
    }

    @GetMapping(path = "/users/{userId}/requests")
    public Collection<ParticipationRequestDto> getRequestsByUser(@PathVariable Long userId) {
        return service.getRequestsByUser(userId);
    }

    @GetMapping(path = "/users/{userId}/events/{eventId}/requests")
    public Collection<ParticipationRequestDto> getRequestsByEventOwner(
            @PathVariable Long userId,
            @PathVariable Long eventId
    ) {
        return service.getRequestsByEventOwner(userId, eventId);
    }

    @PatchMapping(path = "/users/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(
            @PathVariable Long userId,
            @PathVariable Long requestId
    ) {
        return service.cancelRequest(userId, requestId);
    }

    @PatchMapping(path = "/users/{userId}/events/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto confirmRequestInOwnerEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @PathVariable Long reqId
    ) {
        return service.confirmRequestInOwnerEvent(userId, eventId, reqId);
    }

    @PatchMapping(path = "/users/{userId}/events/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto rejectRequestInOwnerEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @PathVariable Long reqId
    ) {
        return service.rejectRequestInOwnerEvent(userId, eventId, reqId);
    }
}
