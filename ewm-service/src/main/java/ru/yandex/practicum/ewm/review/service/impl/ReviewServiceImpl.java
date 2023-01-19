package ru.yandex.practicum.ewm.review.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.event.exception.EventNotFoundException;
import ru.yandex.practicum.ewm.event.model.Event;
import ru.yandex.practicum.ewm.event.model.EventState;
import ru.yandex.practicum.ewm.event.repository.EventRepository;
import ru.yandex.practicum.ewm.review.dto.NewReviewDto;
import ru.yandex.practicum.ewm.review.dto.ReviewDto;
import ru.yandex.practicum.ewm.review.exception.CanOnlyReviewPendingEventsException;
import ru.yandex.practicum.ewm.review.exception.WrongUserRequestingEventReviewsException;
import ru.yandex.practicum.ewm.review.mapper.ReviewMapper;
import ru.yandex.practicum.ewm.review.model.Review;
import ru.yandex.practicum.ewm.review.repository.ReviewRepository;
import ru.yandex.practicum.ewm.review.service.ReviewService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final EventRepository eventRepository;
    private final ReviewMapper mapper;

    @Override
    public ReviewDto addNewReview(NewReviewDto reviewDto) {
        Event event = eventRepository.findById(reviewDto.getEventId()).orElseThrow(() ->
                new EventNotFoundException(
                        String.format("Ошибка при добавлении комментария модератором для события с id=%d: " +
                                "событие не найдено.", reviewDto.getEventId())));

        if (event.getState() != EventState.PENDING) {
            throw new CanOnlyReviewPendingEventsException(
                    String.format("Ошибка при добавлении комментария модератором для события с id=%d: " +
                            "событие должно быть в состоянии ожидания модерации.", reviewDto.getEventId()));
        }

        Review review = reviewRepository.save(mapper.mapToNewModel(reviewDto, event, LocalDateTime.now()));
        event.setState(EventState.REVIEWED);
        eventRepository.save(event);

        log.debug("Добавлен новый комментарий модератора: {}", review);
        return mapper.mapToDto(review);
    }

    @Override
    public Collection<ReviewDto> getEventReviewsByOwner(long userId, long eventId, int from, int size) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(
                String.format("Ошибка при добавлении комментария модератором для события с id=%d: " +
                        "событие не найдено.", eventId)));

        if (event.getInitiator().getId() != userId) {
            throw new WrongUserRequestingEventReviewsException(
                    String.format("Ошибка при получении комментариев модератора для события с id=%d " +
                            "пользователем с id=%d: пользователь не является инициатором события.", eventId, userId));
        }

        return getEventReviewsInternal(eventId, from, size);
    }

    @Override
    public Collection<ReviewDto> getEventReviewsInternal(long eventId, int from, int size) {
        return reviewRepository.findAllByEventIdOrderByCreated(eventId, Pageable.ofSize(size)).stream()
                .skip(from)
                .map(mapper::mapToDto)
                .collect(Collectors.toSet());
    }
}
