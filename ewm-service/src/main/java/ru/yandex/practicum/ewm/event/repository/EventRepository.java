package ru.yandex.practicum.ewm.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.ewm.event.model.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, EventRepositoryCustom {

    Page<Event> findAllByInitiatorId(long initiatorId, Pageable pageable);

//    @Query(value = "SELECT e FROM Event e " +
//            "WHERE (?1 IS NULL OR (LOWER(e.annotation) LIKE CONCAT('%', LOWER(?1), '%') " +
//            "OR LOWER(e.description) LIKE CONCAT('%', LOWER(?1), '%'))) " +
//            "AND (?2 IS NULL OR (e.id IN ?2)) " +
//            "AND (?3 IS NULL OR (e.paid = ?3)) " +
//            "AND (?4 IS NULL OR (?4 <= e.eventDate)) " +
//            "AND (?5 IS NULL OR (e.eventDate <= ?5)) " +
//            "AND ((?4 IS NOT NULL OR ?5 IS NOT NULL) OR (CURRENT_TIMESTAMP < e.eventDate))" +
//            "AND e.state = 'PUBLISHED'" +
//            "AND (?6 IS FALSE OR (e.participationLimit > (SELECT COUNT(r) FROM ParticipationRequest r " +
//                    "WHERE r.event.id = e.id AND r.status = 'APPROVED'))) " +
//            "ORDER BY e.eventDate")
//    @Query(nativeQuery = true, value = "SELECT * FROM events AS e " +
//            "WHERE (?1 IS NULL OR (LOWER(e.annotation) LIKE CONCAT('%', LOWER(?1), '%') " +
//            "OR LOWER(e.description) LIKE CONCAT('%', LOWER(?1), '%'))) " +
//            "AND (?2 IS NULL OR (e.event_id IN ?2)) " +
//            "AND (?3 IS NULL OR (e.paid = ?3)) " +
//            "AND (?4 IS NULL OR (?4 <= e.event_date)) " +
//            "AND (?5 IS NULL OR (e.event_date <= ?5)) " +
//            "AND ((?4 IS NOT NULL OR ?5 IS NOT NULL) OR (CURRENT_TIMESTAMP < e.event_date))" +
//            "AND e.state = 'PUBLISHED'" +
//            "AND (?6 IS FALSE OR (e.participation_limit > (SELECT COUNT(r) FROM participation_requests r " +
//            "WHERE r.event = e.event_id AND r.status = 'APPROVED'))) " +
//            "ORDER BY e.event_date")
//    Page<Event> getFilteredEventsPublic(String text,
//                                        Collection<Long> categories,
//                                        Boolean paid,
//                                        LocalDateTime rangeStart,
//                                        LocalDateTime rangeEnd,
//                                        boolean onlyAvailable,
//                                        Pageable pageable);

//    @Query(nativeQuery = true, value = "SELECT * FROM events e " +
//            "WHERE (?1 IS NULL OR (e.initiator IN (?1))) " +
//            "AND (?2 IS NULL OR (e.state IN (?2))) " +
//            "AND (?3 IS NULL OR (e.event_category IN (?3))) " +
//            "AND (?4 IS NULL OR (?4 <= e.event_date)) " +
//            "AND (?5 IS NULL OR (e.event_date <= ?5))")
//    Page<Event> getFilteredEventsInternal(
//            Collection<Long> users,
//            Collection<EventState> states,
//            Collection<Long> categories,
//            LocalDateTime rangeStart,
//            LocalDateTime rangeEnd,
//            Pageable pageable
//    );

    boolean existsByCategoryId(Long catId);
}