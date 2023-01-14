package ru.yandex.practicum.ewm.compilation.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.compilation.dto.CompilationDto;
import ru.yandex.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.yandex.practicum.ewm.compilation.model.Compilation;
import ru.yandex.practicum.ewm.compilation.repository.CompilationRepository;
import ru.yandex.practicum.ewm.compilation.service.CompilationService;
import ru.yandex.practicum.ewm.event.mapper.EventMapper;
import ru.yandex.practicum.ewm.event.model.Event;
import ru.yandex.practicum.ewm.event.repository.EventRepository;
import ru.yandex.practicum.ewm.compilation.exception.CompilationNotFoundException;
import ru.yandex.practicum.ewm.event.exception.EventNotFoundException;
import ru.yandex.practicum.ewm.compilation.exception.PinnedAlreadySetException;
import ru.yandex.practicum.ewm.request.service.RequestService;
import ru.yandex.practicum.ewm.stats.service.EwmStatsService;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CompilationServiceImpl implements CompilationService {

    private final RequestService requestService;
    private final EwmStatsService statsService;
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;

    @Override
    public CompilationDto getCompilation(long compId) {
        Compilation compilation = getModel(compId);
        Collection<Long> eventIds = compilation.getEvents().stream()
                .map(Event::getId)
                .collect(Collectors.toSet());
        Map<Long, Integer> confirmedRequestsMap = requestService.getConfirmedRequestsForCollection(eventIds);
        Map<Long, Integer> viewsCountMap = statsService.getViewsForCollection(eventIds);

        log.trace("Запрошена подборка с id={}", compId);
        return compilationMapper.mapToDto(
                compilation,
                compilation.getEvents().stream()
                        .map(event -> eventMapper.mapToShortDto(
                                event,
                                confirmedRequestsMap.get(event.getId()),
                                viewsCountMap.get(event.getId())))
                        .collect(Collectors.toSet()));
    }

    @Override
    public Collection<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        Collection<Compilation> compilations = compilationRepository.getCompilations(pinned, Pageable.ofSize(size))
                .stream()
                .skip(from)
                .collect(Collectors.toSet());

        Set<Event> events = new HashSet<>();
        compilations.forEach(compilation -> events.addAll(compilation.getEvents()));

        Collection<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toSet());

        Map<Long, Integer> requestsCountMap = requestService.getConfirmedRequestsForCollection(eventIds);
        Map<Long, Integer> viewsCountMap = statsService.getViewsForCollection(eventIds);

        log.trace("Запрошен список подборок событий.");
        return compilations.stream()
                .map(compilation -> compilationMapper.mapToDto(
                        compilation,
                        compilation.getEvents().stream()
                                .map(event -> eventMapper.mapToShortDto(
                                        event,
                                        requestsCountMap.get(event.getId()),
                                        viewsCountMap.get(event.getId())))
                                .collect(Collectors.toSet())))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        Set<Event> events = new HashSet<>(eventRepository.findAllById(newCompilationDto.getEvents()));
        Compilation compilation = compilationRepository.save(compilationMapper.mapToNewModel(newCompilationDto, events));
        compilationRepository.addEventsIntoCompilation(
                events.stream()
                        .map(Event::getId)
                        .collect(Collectors.toList()),
                compilation.getId());

        Collection<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toSet());

        Map<Long, Integer> requestsCountMap = requestService.getConfirmedRequestsForCollection(eventIds);
        Map<Long, Integer> viewsCountMap = statsService.getViewsForCollection(eventIds);


        log.debug("Добавлена новая подборка: {}", compilation);
        return compilationMapper.mapToDto(compilation, compilation.getEvents().stream()
                .map(event -> eventMapper.mapToShortDto(
                        event,
                        requestsCountMap.get(event.getId()),
                        viewsCountMap.get(event.getId())))
                .collect(Collectors.toSet()));
    }

    @Override
    public void addEventIntoCompilation(long compId, long eventId) {
        Supplier<String> errorString = () -> String.format("Ошибка при добавлении события с id=%d в подборку с id=%d: ",
                eventId, compId);

        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException(errorString.get() + "событие не найдено.");

        } else if (!compilationRepository.existsById(compId)) {
            throw new CompilationNotFoundException(errorString.get() + "подборка не найдена.");
        }
        compilationRepository.addEventIntoCompilation(eventId, compId);
        log.debug("В подборку с id={} добавлено событие с id={}", compId, eventId);
    }

    @Override
    public void removeEventFromCompilation(long compId, long eventId) {
        Supplier<String> errorString = () -> String.format("Ошибка при удалении события с id=%d из подборки с id=%d: ",
                eventId, compId);

        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException(errorString.get() + "событие не найдено.");

        } else if (!compilationRepository.existsById(compId)) {
            throw new CompilationNotFoundException(errorString.get() + "подборка не найдена.");
        }
        compilationRepository.removeEventsFromCompilation(List.of(eventId), compId);
        log.debug("Из подборки с id={} удалено событие с id={}", compId, eventId);
    }

    @Override
    public void changePinned(long compId, boolean pinned) {
        if (compilationRepository.existsByPinnedIsAndId(pinned, compId)) {
            throw new PinnedAlreadySetException(String.format("Ошибка при изменении у подборки с id=%d " +
                    "статуса прикрепления на '%b': статус уже установлен.", compId, pinned));
        }
        compilationRepository.changePinned(pinned, compId);
        log.debug("Статус прикрепления у подборки с id={} изменен на '{}'", compId, pinned);
    }

    @Override
    public void deleteCompilation(long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new CompilationNotFoundException(
                    String.format("Ошибка при удалении подборки с id=%d: подборка не найдена.", compId));
        }
        compilationRepository.removeEventsFromCompilation(
                compilationRepository.findById(compId).orElseThrow().getEvents().stream()
                        .map(Event::getId)
                        .collect(Collectors.toSet()),
                compId);
        compilationRepository.deleteById(compId);
        log.debug("Удалена подборка с id={}", compId);
    }

    private Compilation getModel(long compId) {
        return compilationRepository.findById(compId).orElseThrow(() ->
                new CompilationNotFoundException(String.format("Ошибка при получении компиляции с id=%d: " +
                        "компиляция не найдена.", compId)));
    }
}
