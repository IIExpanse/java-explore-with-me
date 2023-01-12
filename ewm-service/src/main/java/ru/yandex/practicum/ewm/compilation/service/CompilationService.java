package ru.yandex.practicum.ewm.compilation.service;

import ru.yandex.practicum.ewm.compilation.dto.CompilationDto;
import ru.yandex.practicum.ewm.compilation.dto.NewCompilationDto;

import java.util.Collection;

public interface CompilationService {

    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    CompilationDto getCompilation(long compId);

    Collection<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    void addEventIntoCompilation(long compId, long eventId);

    void removeEventFromCompilation(long compId, long eventId);

    void changePinned(long compId, boolean pinned);

    void deleteCompilation(long compId);
}
