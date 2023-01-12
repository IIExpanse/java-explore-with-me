package ru.yandex.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.stats.model.EndpointHit;

import java.time.LocalDateTime;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("SELECT COUNT(e.ip) FROM EndpointHit e " +
            "WHERE ?1 <= e.time " +
            "AND e.time <= ?2 " +
            "AND LOWER(e.uri) LIKE CONCAT(LOWER(?3), '%') ")
    int countAllByTimeBetweenAndUriLike(LocalDateTime start, LocalDateTime end, String uri);

    @Query("SELECT COUNT(DISTINCT(e.ip)) FROM EndpointHit e " +
            "WHERE ?1 <= e.time " +
            "AND e.time <= ?2 " +
            "AND LOWER(e.uri) LIKE CONCAT(LOWER(?3), '%') ")
    int countUniqueHits(LocalDateTime start, LocalDateTime end, String uri);
}
