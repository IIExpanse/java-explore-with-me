package ru.yandex.practicum.ewm.review.service;

import ru.yandex.practicum.ewm.review.dto.NewReviewDto;
import ru.yandex.practicum.ewm.review.dto.ReviewDto;

import java.util.Collection;

public interface ReviewService {

    ReviewDto addNewReview(NewReviewDto reviewDto);

    Collection<ReviewDto> getEventReviewsByOwner(long userId, long eventId, int from, int size);

    Collection<ReviewDto> getEventReviewsInternal(long eventId, int from, int size);
}
