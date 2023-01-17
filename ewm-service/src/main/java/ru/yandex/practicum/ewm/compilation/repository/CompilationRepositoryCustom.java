package ru.yandex.practicum.ewm.compilation.repository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompilationRepositoryCustom {

    void addEventsIntoCompilation(List<Long> eventIds, long compId);
}
