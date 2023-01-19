package ru.yandex.practicum.ewm.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.yandex.practicum.ewm.category.mapper.CategoryMapper;
import ru.yandex.practicum.ewm.category.model.Category;
import ru.yandex.practicum.ewm.event.dto.*;
import ru.yandex.practicum.ewm.event.model.Event;
import ru.yandex.practicum.ewm.event.model.EventState;
import ru.yandex.practicum.ewm.user.model.User;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring",
        uses = CategoryMapper.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {

    @Mapping(target = "lon", source = "eventDto.location.lon")
    @Mapping(target = "lat", source = "eventDto.location.lat")
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "compilations", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "reviews", ignore = true)
    Event mapToNewModel(
            NewEventDto eventDto,
            Category category,
            User initiator,
            LocalDateTime createdOn,
            EventState state);

    EventFullDto mapToFullDto(Event event, Integer confirmedRequests, Integer views, LocationDto location);

    EventShortDto mapToShortDto(Event event, Integer confirmedRequests, Integer views);

    @Mapping(target = "lon", ignore = true)
    @Mapping(target = "lat", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "requests", ignore = true)
    @Mapping(target = "requestModeration", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "compilations", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "reviews", ignore = true)
    Event updateEvent(@MappingTarget Event event, UpdateEventRequest request, Category category);

    @Mapping(target = "state", ignore = true)
    @Mapping(target = "requests", ignore = true)
    @Mapping(target = "requestModeration", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "compilations", ignore = true)
    @Mapping(target = "lon", source = "request.location.lon")
    @Mapping(target = "lat", source = "request.location.lat")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "reviews", ignore = true)
    Event updateEventAdmin(@MappingTarget Event event, AdminUpdateEventRequest request, Category category);
}
