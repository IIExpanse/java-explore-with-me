package ru.yandex.practicum.ewm.event.repository.impl;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.ewm.event.model.Event;
import ru.yandex.practicum.ewm.event.model.EventState;
import ru.yandex.practicum.ewm.event.model.QEvent;
import ru.yandex.practicum.ewm.event.repository.EventRepositoryCustom;
import ru.yandex.practicum.ewm.request.model.ParticipationRequest;
import ru.yandex.practicum.ewm.request.model.QParticipationRequest;
import ru.yandex.practicum.ewm.request.model.RequestStatus;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public Collection<Event> getFilteredEventsPublic(
            String text,
            Collection<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            boolean onlyAvailable,
            int size) {

        JPQLQuery<Event> eventQuery = new JPAQuery<>(entityManager);
        JPQLQuery<ParticipationRequest> requestQuery = new JPAQuery<>(entityManager);
        QEvent event = QEvent.event;
        QParticipationRequest request = QParticipationRequest.participationRequest;

        eventQuery.from(event).where(event.state.eq(EventState.PUBLISHED));
        if (text != null) {
            eventQuery.where(event.annotation.equalsIgnoreCase(text).or(event.description.equalsIgnoreCase(text)));
        }
        if (categories != null) {
            eventQuery.where(event.category.id.in(categories));
        }
        if (paid != null) {
            eventQuery.where(event.paid.eq(paid));
        }
        if (rangeStart != null) {
            eventQuery.where(event.eventDate.after(rangeStart));
        }
        if (rangeEnd != null) {
            eventQuery.where(event.eventDate.before(rangeEnd));
        }
        if (rangeStart == null && rangeEnd == null) {
            eventQuery.where(event.eventDate.after(LocalDateTime.now()));
        }
        if (onlyAvailable) {
            eventQuery
                    .where(event.participantLimit
                            .lt(requestQuery.from(request)
                                    .where(request.event.id.eq(event.id)
                                            .and(request.status.eq(RequestStatus.CONFIRMED)))
                                    .stream().count()));
        }

        return eventQuery.stream().limit(size).collect(Collectors.toSet());
    }

    @Override
    public Collection<Event> getFilteredEventsInternal(
            Collection<Long> users,
            Collection<EventState> states,
            Collection<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int size) {

        JPQLQuery<Event> eventQuery = new JPAQuery<>(entityManager);
        QEvent event = QEvent.event;

        eventQuery.from(event);
        if (users != null) {
            eventQuery.where(event.initiator.id.in(users));
        }
        if (states != null) {
            eventQuery.where(event.state.in(states));
        }
        if (categories != null) {
            eventQuery.where(event.category.id.in(categories));
        }
        if (rangeStart != null) {
            eventQuery.where(event.eventDate.after(rangeStart));
        }
        if (rangeEnd != null) {
            eventQuery.where(event.eventDate.before(rangeEnd));
        }

        return eventQuery.stream().limit(size).collect(Collectors.toSet());
    }
}
