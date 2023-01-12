package ru.yandex.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewm.compilation.dto.CompilationDto;
import ru.yandex.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.ewm.compilation.service.CompilationService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
public class CompilationController {

    private final CompilationService service;

    @PostMapping(path = "/admin/compilations")
    public ResponseEntity<CompilationDto> addCompilation(
            @RequestBody @Valid NewCompilationDto newCompilationDto
    ) {
        return ResponseEntity.ok(service.addCompilation(newCompilationDto));
    }

    @GetMapping(path = "/compilations/{compId}")
    public ResponseEntity<CompilationDto> getCompilation(@PathVariable Long compId) {
        return ResponseEntity.ok(service.getCompilation(compId));
    }

    @GetMapping(path = "/compilations")
    public ResponseEntity<Collection<CompilationDto>> getCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return ResponseEntity.ok(service.getCompilations(pinned, from, size));
    }

    @PatchMapping(path = "/admin/compilations/{compId}/events/{eventId}")
    @ResponseStatus(code = HttpStatus.OK)
    public void addEventIntoCompilation(
            @PathVariable Long compId,
            @PathVariable Long eventId
    ) {
        service.addEventIntoCompilation(compId, eventId);
    }

    @DeleteMapping(path = "/admin/compilations/{compId}/events/{eventId}")
    @ResponseStatus(code = HttpStatus.OK)
    public void removeEventFromCompilation(
            @PathVariable Long compId,
            @PathVariable Long eventId
    ) {
        service.removeEventFromCompilation(compId, eventId);
    }

    @PatchMapping(path = "/admin/compilations/{compId}/pin")
    @ResponseStatus(code = HttpStatus.OK)
    public void pinCompilation(
            @PathVariable Long compId
    ) {
        service.changePinned(compId, true);
    }

    @DeleteMapping(path = "/admin/compilations/{compId}/pin")
    @ResponseStatus(code = HttpStatus.OK)
    public void unpinCompilation(
            @PathVariable Long compId
    ) {
        service.changePinned(compId, false);
    }

    @DeleteMapping(path = "/admin/compilations/{compId}")
    @ResponseStatus(code = HttpStatus.OK)
    public void deleteCompilation(
            @PathVariable Long compId
    ) {
        service.deleteCompilation(compId);
    }
}
