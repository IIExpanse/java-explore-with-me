package ru.yandex.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.stats.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("SELECT e.uri FROM EndpointHit e " +
            "WHERE ?1 <= e.time " +
            "AND e.time <= ?2 " +
            "AND e.uri IN ?3")
    Collection<String> getAllUris(LocalDateTime start, LocalDateTime end, Collection<String> uris);

    @Query("SELECT e FROM EndpointHit e " +
            "WHERE ?1 <= e.time " +
            "AND e.time <= ?2 " +
            "AND e.uri IN ?3")
    Collection<EndpointHit> getAllHits(LocalDateTime start, LocalDateTime end, Collection<String> uris);
}
