package ru.yandex.practicum.ewm.stats.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.stats.client.StatsClient;
import ru.yandex.practicum.ewm.stats.dto.EndpointHitDto;
import ru.yandex.practicum.ewm.stats.service.EwmStatsService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EwmStatsServiceImpl implements EwmStatsService {

    private static final String appName = "ewm-main-service";
    private static final String endPointPath = "/events";
    private final StatsClient client;

    @Override
    public void saveHit(Long eventId, String ip) {
        String endpoint = endPointPath;

        if (eventId != null) {
            endpoint = endpoint + "/" + eventId;
        }

        client.saveHit(EndpointHitDto.builder()
                .app(appName)
                .uri(endpoint)
                .ip(ip)
                .time(LocalDateTime.now())
                .build());
    }

    @Override
    public int getViewsForEvent(long eventId) {
        return client.getHits(
                LocalDateTime.now().minusYears(1000),
                LocalDateTime.now().plusYears(1000),
                List.of(endPointPath + "/" + eventId),
                false).get(0).getHits();
    }
}
