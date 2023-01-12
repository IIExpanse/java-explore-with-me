package ru.yandex.practicum.ewm.event.repository;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.ewm.event.model.Event;
import ru.yandex.practicum.ewm.event.model.EventState;

import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public interface EventRepositoryCustom {

    Collection<Event> getFilteredEventsPublic(String text,
                                        Collection<Long> categories,
                                        Boolean paid,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        boolean onlyAvailable,
                                        int size);

    Collection<Event> getFilteredEventsInternal(
            Collection<Long> users,
            Collection<EventState> states,
            Collection<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int size
    );
}
