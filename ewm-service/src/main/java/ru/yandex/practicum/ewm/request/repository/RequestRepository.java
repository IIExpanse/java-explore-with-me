package ru.yandex.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.ewm.request.model.ParticipationRequest;
import ru.yandex.practicum.ewm.request.model.RequestStatus;

import java.util.Collection;

@Repository
public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    boolean existsByRequesterId(long requesterId);

    Collection<ParticipationRequest> findAllByRequesterId(long requesterId);

    Collection<ParticipationRequest> findAllByEventInitiatorIdAndEventId(long initiatorId, long eventId);

    int countAllByEventIdAndStatus(long eventId, RequestStatus status);

    @Query("SELECT r.event.id FROM ParticipationRequest r " +
            "WHERE r.event.id IN ?1 " +
            "AND r.status = ?2")
    Collection<Long> getAllEventIdsFromConfirmedRequests(Collection<Long> eventIds, RequestStatus status);

    @Query("UPDATE ParticipationRequest r " +
            "SET r.status = 'REJECTED'" +
            "WHERE r.event.id = ?1 AND r.status = 'PENDING'")
    @Modifying
    void rejectPendingRequestsInFullEvent(long eventId);
}
