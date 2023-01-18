package ru.yandex.practicum.ewm.review.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewm.review.dto.NewReviewDto;
import ru.yandex.practicum.ewm.review.dto.ReviewDto;
import ru.yandex.practicum.ewm.review.service.ReviewService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@ResponseStatus(HttpStatus.OK)
public class ReviewController {

    private final ReviewService service;

    @GetMapping(path = "/users/{userId}/reviews/events/{eventId}")
    Collection<ReviewDto> getEventReviewsForOwner(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return service.getEventReviewsByOwner(userId, eventId, from, size);
    }

    @GetMapping(path = "/reviews/events/{eventId}")
    Collection<ReviewDto> getEventReviewsInternal(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return service.getEventReviewsInternal(eventId, from, size);
    }

    @PostMapping(path = "/reviews")
    ReviewDto addReview(@RequestBody @Valid NewReviewDto reviewDto) {
        return service.addNewReview(reviewDto);
    }
}
