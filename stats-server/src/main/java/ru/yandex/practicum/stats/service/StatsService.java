package ru.yandex.practicum.stats.service;

import ru.yandex.practicum.stats.dto.EndpointHitDto;
import ru.yandex.practicum.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.Collection;

public interface StatsService {

    EndpointHitDto saveHit(EndpointHitDto endpointHitDto);

    Collection<ViewStats> getHits(LocalDateTime start, LocalDateTime end, Collection<String> uris, boolean unique);
}
