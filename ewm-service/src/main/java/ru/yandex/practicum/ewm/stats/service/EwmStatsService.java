package ru.yandex.practicum.ewm.stats.service;

import java.util.Collection;
import java.util.Map;

public interface EwmStatsService {

    void saveHit(Long eventId, String ip);

    int getViewsForEvent(long eventId);

    Map<Long, Integer> getViewsForCollection(Collection<Long> eventIds);
}
