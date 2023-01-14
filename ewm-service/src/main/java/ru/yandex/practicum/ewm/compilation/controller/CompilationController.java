package ru.yandex.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewm.compilation.dto.CompilationDto;
import ru.yandex.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.ewm.compilation.service.CompilationService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@ResponseStatus(code = HttpStatus.OK)
public class CompilationController {

    private final CompilationService service;

    @PostMapping(path = "/admin/compilations")
    public CompilationDto addCompilation(
            @RequestBody @Valid NewCompilationDto newCompilationDto
    ) {
        return service.addCompilation(newCompilationDto);
    }

    @GetMapping(path = "/compilations/{compId}")
    public CompilationDto getCompilation(@PathVariable Long compId) {
        return service.getCompilation(compId);
    }

    @GetMapping(path = "/compilations")
    public Collection<CompilationDto> getCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return service.getCompilations(pinned, from, size);
    }

    @PatchMapping(path = "/admin/compilations/{compId}/events/{eventId}")
    public void addEventIntoCompilation(
            @PathVariable Long compId,
            @PathVariable Long eventId
    ) {
        service.addEventIntoCompilation(compId, eventId);
    }


    @PatchMapping(path = "/admin/compilations/{compId}/pin")
    public void pinCompilation(
            @PathVariable Long compId
    ) {
        service.changePinned(compId, true);
    }

    @DeleteMapping(path = "/admin/compilations/{compId}/pin")
    public void unpinCompilation(
            @PathVariable Long compId
    ) {
        service.changePinned(compId, false);
    }

    @DeleteMapping(path = "/admin/compilations/{compId}/events/{eventId}")
    public void removeEventFromCompilation(
            @PathVariable Long compId,
            @PathVariable Long eventId
    ) {
        service.removeEventFromCompilation(compId, eventId);
    }

    @DeleteMapping(path = "/admin/compilations/{compId}")
    public void deleteCompilation(
            @PathVariable Long compId
    ) {
        service.deleteCompilation(compId);
    }
}
