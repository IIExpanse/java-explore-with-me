package ru.yandex.practicum.ewm.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.ewm.compilation.dto.CompilationDto;
import ru.yandex.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.ewm.compilation.model.Compilation;
import ru.yandex.practicum.ewm.event.dto.EventShortDto;
import ru.yandex.practicum.ewm.event.model.Event;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface CompilationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", source = "events")
    Compilation mapToNewModel(NewCompilationDto newCompilationDto, Set<Event> events);

    @Mapping(target = "events", source = "events")
    CompilationDto mapToDto(Compilation compilation, Set<EventShortDto> events);
}
