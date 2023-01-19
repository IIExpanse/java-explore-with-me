package ru.yandex.practicum.ewm.review.service;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.ewm.category.dto.NewCategoryDto;
import ru.yandex.practicum.ewm.category.service.CategoryService;
import ru.yandex.practicum.ewm.event.dto.LocationDto;
import ru.yandex.practicum.ewm.event.dto.NewEventDto;
import ru.yandex.practicum.ewm.event.exception.EventNotFoundException;
import ru.yandex.practicum.ewm.event.service.EventService;
import ru.yandex.practicum.ewm.review.dto.NewReviewDto;
import ru.yandex.practicum.ewm.review.dto.ReviewDto;
import ru.yandex.practicum.ewm.review.exception.WrongUserRequestingEventReviewsException;
import ru.yandex.practicum.ewm.user.dto.UserDto;
import ru.yandex.practicum.ewm.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AllArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ReviewServiceTest {

    private UserService userService;
    private CategoryService categoryService;
    private EventService eventService;
    private ReviewService reviewService;

    @Test
    public void addNewReviewTest() {
        long userId = userService.addUser(makeDefaultUser()).getId();
        long catId = categoryService.addCategory(makeNewCategoryRequest()).getId();
        long eventId = eventService.addEvent(makeNewEventRequest(catId), userId).getId();
        ReviewDto reviewDto = reviewService.addNewReview(makeNewReviewDto(eventId));
        LocalDateTime time = LocalDateTime.now();
        reviewDto.setCreated(time);

        ReviewDto savedReview = List.copyOf(reviewService.getEventReviewsInternal(eventId, 0, 10)).get(0);
        savedReview.setCreated(time);

        assertEquals(reviewDto, savedReview);
    }

    @Test
    public void getEventReviewsByOwnerTest() {
        long userId = userService.addUser(makeDefaultUser()).getId();
        long catId = categoryService.addCategory(makeNewCategoryRequest()).getId();
        long eventId = eventService.addEvent(makeNewEventRequest(catId), userId).getId();
        ReviewDto reviewDto = reviewService.addNewReview(makeNewReviewDto(eventId));
        LocalDateTime time = LocalDateTime.now();
        reviewDto.setCreated(time);

        ReviewDto savedReview = List.copyOf(
                reviewService.getEventReviewsByOwner(userId, eventId, 0, 10)).get(0);
        savedReview.setCreated(time);

        assertEquals(reviewDto, savedReview);
    }

    @Test
    public void shouldThrowExceptionForRequestingReviewsWithWrongUser() {
        long userId = userService.addUser(makeDefaultUser()).getId();
        long catId = categoryService.addCategory(makeNewCategoryRequest()).getId();
        long eventId = eventService.addEvent(makeNewEventRequest(catId), userId).getId();

        assertThrows(WrongUserRequestingEventReviewsException.class,
                () -> reviewService.getEventReviewsByOwner(1000L, eventId, 0, 10));
    }

    @Test
    public void shouldThrowExceptionForAddingReviewForNotFoundEvent() {
        long userId = userService.addUser(makeDefaultUser()).getId();

        assertThrows(EventNotFoundException.class,
                () -> reviewService.getEventReviewsByOwner(userId, 1L, 0, 10));
    }

    private NewReviewDto makeNewReviewDto(long eventId) {
        return NewReviewDto.builder()
                .reviewerName("John")
                .text("review text")
                .eventId(eventId)
                .build();

    }

    private NewEventDto makeNewEventRequest(long catId) {
        return NewEventDto.builder()
                .annotation("new event")
                .category(catId)
                .eventDate(LocalDateTime.now().plusDays(1))
                .location(LocationDto.builder().lat(14.20642).lon(110.17272).build())
                .paid(false)
                .participantLimit(0)
                .requestModeration(false)
                .description("new event")
                .title("new event")
                .build();
    }

    private NewCategoryDto makeNewCategoryRequest() {
        return NewCategoryDto.builder().name("Концерты").build();
    }

    private UserDto makeDefaultUser() {
        return UserDto.builder()
                .email("some@mail.ru")
                .name("John")
                .build();
    }
}
