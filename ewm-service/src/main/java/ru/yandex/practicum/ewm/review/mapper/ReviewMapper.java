package ru.yandex.practicum.ewm.review.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.ewm.event.model.Event;
import ru.yandex.practicum.ewm.review.dto.NewReviewDto;
import ru.yandex.practicum.ewm.review.dto.ReviewDto;
import ru.yandex.practicum.ewm.review.model.Review;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    Review mapToNewModel(NewReviewDto reviewDto, Event event, LocalDateTime created);

    @Mapping(target = "eventId", source = "review.event.id")
    ReviewDto mapToDto(Review review);
}
