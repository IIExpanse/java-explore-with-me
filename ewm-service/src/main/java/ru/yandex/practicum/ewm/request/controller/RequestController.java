package ru.yandex.practicum.ewm.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.yandex.practicum.ewm.request.service.RequestService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
public class RequestController {

    private final RequestService service;

    @PostMapping(path = "/users/{userId}/requests")
    public ResponseEntity<ParticipationRequestDto> addRequest(
            @PathVariable Long userId,
            @RequestParam Long eventId) {
        return ResponseEntity.ok(service.addRequest(userId, eventId));
    }

    @GetMapping(path = "/users/{userId}/requests")
    public ResponseEntity<Collection<ParticipationRequestDto>> getRequestsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getRequestsByUser(userId));
    }

    @GetMapping(path = "/users/{userId}/events/{eventId}/requests")
    public ResponseEntity<Collection<ParticipationRequestDto>> getRequestsByEventOwner(
            @PathVariable Long userId,
            @PathVariable Long eventId
    ) {
        return ResponseEntity.ok(service.getRequestsByEventOwner(userId, eventId));
    }

    @PatchMapping(path = "/users/{userId}/requests/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(
            @PathVariable Long userId,
            @PathVariable Long requestId
    ) {
        return ResponseEntity.ok(service.cancelRequest(userId, requestId));
    }

    @PatchMapping(path = "/users/{userId}/events/{eventId}/requests/{reqId}/confirm")
    public ResponseEntity<ParticipationRequestDto> confirmRequestInOwnerEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @PathVariable Long reqId
    ) {
        return ResponseEntity.ok(service.confirmRequestInOwnerEvent(userId, eventId, reqId));
    }

    @PatchMapping(path = "/users/{userId}/events/{eventId}/requests/{reqId}/reject")
    public ResponseEntity<ParticipationRequestDto> rejectRequestInOwnerEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @PathVariable Long reqId
    ) {
        return ResponseEntity.ok(service.rejectRequestInOwnerEvent(userId, eventId, reqId));
    }
}
