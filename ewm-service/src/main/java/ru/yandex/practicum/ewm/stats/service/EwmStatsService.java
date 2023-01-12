package ru.yandex.practicum.ewm.stats.service;

public interface EwmStatsService {

    void saveHit(Long eventId, String ip);

    int getViewsForEvent(long eventId);
}
