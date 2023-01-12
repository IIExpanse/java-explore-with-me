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
import java.util.Collection;
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
        EndpointHit endpointHit = repository.save(mapper.mapToNewModel(endpointHitDto));

        log.debug("Сохранено посещение эндпоинта '{}'.", endpointHit.getUri());
        return mapper.mapToDto(endpointHit);
    }

    @Override
    public Collection<ViewStats> getHits(
            LocalDateTime start,
            LocalDateTime end,
            Collection<String> uris, boolean unique) {

        System.out.println(uris);
        log.trace("Запрошена статистика посещений по {} адресам", uris.size());
        if (unique) {
            return uris.stream()
                    .map(uri -> ViewStats.builder()
                            .app(appName)
                            .uri(uri)
                            .hits(repository.countUniqueHits(start, end, uri))
                            .build())
                    .collect(Collectors.toList());
        }
        return uris.stream()
                .map(uri -> ViewStats.builder()
                        .app(appName)
                        .uri(uri)
                        .hits(repository.countAllByTimeBetweenAndUriLike(start, end, uri))
                        .build())
                .collect(Collectors.toList());
    }
}
