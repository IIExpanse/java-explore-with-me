package ru.yandex.practicum.stats.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.stats.dto.EndpointHitDto;
import ru.yandex.practicum.stats.dto.ViewStats;
import ru.yandex.practicum.stats.serializer.LocalDateTimeSerializer;
import ru.yandex.practicum.stats.service.StatsService;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@ResponseStatus(HttpStatus.OK)
public class StatsController {

    private final StatsService service;

    @PostMapping(path = "/hit")
    public EndpointHitDto saveHit(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        if (endpointHitDto.getTime() == null) {
            endpointHitDto.setTime(LocalDateTime.now());
        }
        return service.saveHit(endpointHitDto);
    }

    @GetMapping(path = "/stats")
    public Collection<ViewStats> getHits(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam @NotEmpty Collection<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique
    ) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(LocalDateTimeSerializer.DATE_TIME_FORMAT);

        return service.getHits(
                LocalDateTime.parse(start, formatter),
                LocalDateTime.parse(end, formatter),
                uris,
                unique);
    }
}
