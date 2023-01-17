package ru.yandex.practicum.stats.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.stats.dto.EndpointHitDto;
import ru.yandex.practicum.stats.model.EndpointHit;

@Mapper(componentModel = "spring")
public interface StatsMapper {

    EndpointHit mapToNewModel(EndpointHitDto endpointHitDto);

    EndpointHitDto mapToDto(EndpointHit endpointHit);
}
