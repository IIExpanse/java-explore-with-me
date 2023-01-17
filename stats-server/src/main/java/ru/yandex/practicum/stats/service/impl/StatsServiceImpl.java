package ru.yandex.practicum.stats.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.stats.dto.EndpointHitDto;
import ru.yandex.practicum.stats.dto.ViewStats;
import ru.yandex.practicum.stats.mapper.StatsMapper;
import ru.yandex.practicum.stats.model.EndpointHit;
import ru.yandex.practicum.stats.repository.StatsRepository;
import ru.yandex.practicum.stats.service.StatsService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StatsServiceImpl implements StatsService {

    private final StatsRepository repository;
    private final StatsMapper mapper;
    private final String appName = "ewm-main-service";

    @Override
    public EndpointHitDto saveHit(EndpointHitDto endpointHitDto) {
        endpointHitDto.setUri(endpointHitDto.getUri().toLowerCase());
        EndpointHit endpointHit = repository.save(mapper.mapToNewModel(endpointHitDto));

        log.debug("Сохранено посещение эндпоинта '{}'.", endpointHit.getUri());
        return mapper.mapToDto(endpointHit);
    }

    @Override
    public Collection<ViewStats> getHits(
            LocalDateTime start,
            LocalDateTime end,
            Collection<String> uris, boolean unique) {

        Map<String, Integer> hitsMap = new HashMap<>();
        uris = uris.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        if (unique) {
            Collection<EndpointHit> hits = repository.getAllHits(start, end, uris);

            Map<String, Set<String>> uniqueIpsPerUriMap = new HashMap<>();
            uris.forEach(uri -> uniqueIpsPerUriMap.put(uri, new HashSet<>()));

            for (EndpointHit hit : hits) {
                String uri = hit.getUri();

                if (uniqueIpsPerUriMap.get(uri).add(hit.getIp())) {
                    hitsMap.compute(uri, (s, integer) -> integer == null ? 1 : integer + 1);
                }
            }

        } else {
            repository.getAllUris(start, end, uris).forEach(
                    uri -> hitsMap.compute(uri, (s, integer) -> integer == null ? 1 : integer + 1));
        }

        log.trace("Запрошена статистика посещений по {} адресам", uris.size());
        return uris.stream()
                .map(uri -> ViewStats.builder()
                        .app(appName)
                        .uri(uri)
                        .hits(hitsMap.get(uri))
                        .build())
                .collect(Collectors.toList());
    }
}
