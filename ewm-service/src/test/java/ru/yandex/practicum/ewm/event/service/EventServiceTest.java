package ru.yandex.practicum.ewm.event.service;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.ewm.category.dto.CategoryDto;
import ru.yandex.practicum.ewm.category.dto.NewCategoryDto;
import ru.yandex.practicum.ewm.category.exception.CategoryNotFoundException;
import ru.yandex.practicum.ewm.category.service.CategoryService;
import ru.yandex.practicum.ewm.event.dto.*;
import ru.yandex.practicum.ewm.event.exception.EventNotFoundException;
import ru.yandex.practicum.ewm.event.model.EventSortType;
import ru.yandex.practicum.ewm.stats.service.EwmStatsService;
import ru.yandex.practicum.ewm.user.dto.UserDto;
import ru.yandex.practicum.ewm.user.exception.UserNotFoundException;
import ru.yandex.practicum.ewm.user.service.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AllArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    private EventService eventService;
    private UserService userService;
    private CategoryService categoryService;
    @MockBean
    private EwmStatsService statsService;

    @BeforeEach
    public void configureStatsMock() {
        Mockito.when(statsService.getViewsForEvent(Mockito.anyLong()))
                .thenReturn(0);
    }

    @Test
    public void addEventTest() {
        UserDto userDto = userService.addUser(makeDefaultUser());
        CategoryDto categoryDto = categoryService.addCategory(makeNewCategoryRequest());
        EventFullDto eventFullDto = eventService.addEvent(makeNewEventRequest(categoryDto.getId()), userDto.getId());
        eventFullDto.setCreatedOn(eventFullDto.getCreatedOn().truncatedTo(ChronoUnit.MILLIS));
        eventFullDto.setEventDate(eventFullDto.getEventDate().truncatedTo(ChronoUnit.MILLIS));

        EventFullDto savedEvent = eventService.getEvent(eventFullDto.getId(), "0.0.0.0");
        savedEvent.setCreatedOn(savedEvent.getCreatedOn().truncatedTo(ChronoUnit.MILLIS));
        savedEvent.setEventDate(savedEvent.getEventDate().truncatedTo(ChronoUnit.MILLIS));

        assertEquals(eventFullDto, savedEvent);
    }

    @Test
    public void shouldThrowExceptionForAddingEventWithoutUser() {
        CategoryDto categoryDto = categoryService.addCategory(makeNewCategoryRequest());
        assertThrows(UserNotFoundException.class,
                () -> eventService.addEvent(makeNewEventRequest(categoryDto.getId()), 1));
    }

    @Test
    public void shouldThrowExceptionForAddingEventWithoutCategory() {
        UserDto userDto = userService.addUser(makeDefaultUser());
        assertThrows(CategoryNotFoundException.class,
                () -> eventService.addEvent(makeNewEventRequest(1), userDto.getId()));
    }

    @Test
    public void shouldThrowExceptionForNotFoundEvent() {
        userService.addUser(makeDefaultUser());

        assertThrows(EventNotFoundException.class, () -> eventService.getEvent(1, ""));
    }

    @Test
    public void getEventsByInitiatorTest() {
        UserDto userDto1 = userService.addUser(makeDefaultUser());
        UserDto userDto2 = makeDefaultUser();
        userDto2.setEmail("new@mail.ru");
        userDto2 = userService.addUser(userDto2);
        CategoryDto categoryDto = categoryService.addCategory(makeNewCategoryRequest());
        EventFullDto eventFullDto1 = eventService.addEvent(makeNewEventRequest(categoryDto.getId()), userDto1.getId());
        EventFullDto eventFullDto2 = eventService.addEvent(makeNewEventRequest(categoryDto.getId()), userDto2.getId());
        EventFullDto eventFullDto3 = eventService.addEvent(makeNewEventRequest(categoryDto.getId()), userDto1.getId());

        Collection<Long> collection = eventService.getEventsByInitiator(userDto1.getId(), 0, 10).stream()
                .map(EventShortDto::getId)
                .collect(Collectors.toSet());

        assertTrue(collection.contains(eventFullDto1.getId()));
        assertTrue(collection.contains(eventFullDto3.getId()));
        assertFalse(collection.contains(eventFullDto2.getId()));
    }

    @Test
    public void getFilteredEventsPublicTest() {
        UserDto userDto1 = userService.addUser(makeDefaultUser());
        UserDto userDto2 = makeDefaultUser();
        userDto2.setEmail("new@mail.ru");
        userDto2 = userService.addUser(userDto2);
        CategoryDto categoryDto = categoryService.addCategory(makeNewCategoryRequest());
        NewEventDto newEventDto = makeNewEventRequest(categoryDto.getId());

        EventFullDto eventFullDto1 = eventService.addEvent(newEventDto, userDto1.getId());
        newEventDto.setAnnotation("second event");
        EventFullDto eventFullDto2 = eventService.addEvent(newEventDto, userDto2.getId());
        newEventDto.setAnnotation("third event");
        newEventDto.setEventDate(LocalDateTime.now().plusMonths(1));
        newEventDto.setPaid(true);
        EventFullDto eventFullDto3 = eventService.addEvent(newEventDto, userDto1.getId());

        eventService.publishEvent(eventFullDto1.getId());
        eventService.publishEvent(eventFullDto2.getId());
        eventService.publishEvent(eventFullDto3.getId());

        Collection<Long> collection = eventService.getFilteredEventsPublic(
                        "new event",
                        null,
                        null,
                        null,
                        null,
                        false,
                        EventSortType.EVENT_DATE,
                        0,
                        10,
                        "0.0.0.0"
                ).stream()
                .map(EventShortDto::getId)
                .collect(Collectors.toSet());

        assertTrue(collection.containsAll(List.of(eventFullDto1.getId(), eventFullDto2.getId(), eventFullDto3.getId())));

        collection = eventService.getFilteredEventsPublic(
                        "second event",
                        null,
                        null,
                        null,
                        null,
                        false,
                        EventSortType.EVENT_DATE,
                        0,
                        10,
                        "0.0.0.0"
                ).stream()
                .map(EventShortDto::getId)
                .collect(Collectors.toSet());

        assertTrue(collection.contains(eventFullDto2.getId()));

        collection = eventService.getFilteredEventsPublic(
                        "third event",
                        null,
                        null,
                        null,
                        null,
                        false,
                        EventSortType.EVENT_DATE,
                        0,
                        10,
                        "0.0.0.0"
                ).stream()
                .map(EventShortDto::getId)
                .collect(Collectors.toSet());

        assertTrue(collection.contains(eventFullDto3.getId()));

        collection = eventService.getFilteredEventsPublic(
                        "third event",
                        null,
                        null,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(1),
                        false,
                        EventSortType.EVENT_DATE,
                        0,
                        10,
                        "0.0.0.0"
                ).stream()
                .map(EventShortDto::getId)
                .collect(Collectors.toSet());

        assertTrue(collection.isEmpty());

        collection = eventService.getFilteredEventsPublic(
                        "third event",
                        null,
                        false,
                        null,
                        null,
                        false,
                        EventSortType.EVENT_DATE,
                        0,
                        10,
                        "0.0.0.0"
                ).stream()
                .map(EventShortDto::getId)
                .collect(Collectors.toSet());

        assertTrue(collection.isEmpty());
    }

    @Test
    public void getFilteredEventsInternal() {
        UserDto userDto1 = userService.addUser(makeDefaultUser());
        UserDto userDto2 = makeDefaultUser();
        userDto2.setEmail("new@mail.ru");
        userDto2 = userService.addUser(userDto2);
        CategoryDto categoryDto = categoryService.addCategory(makeNewCategoryRequest());
        NewEventDto newEventDto = makeNewEventRequest(categoryDto.getId());

        EventFullDto eventFullDto1 = eventService.addEvent(newEventDto, userDto1.getId());
        EventFullDto eventFullDto2 = eventService.addEvent(newEventDto, userDto2.getId());
        newEventDto.setEventDate(LocalDateTime.now().plusMonths(1));
        EventFullDto eventFullDto3 = eventService.addEvent(newEventDto, userDto1.getId());

        eventService.publishEvent(eventFullDto3.getId());

        Collection<Long> collection = eventService.getFilteredEventsInternal(
                        List.of(userDto1.getId()),
                        null,
                        null,
                        null,
                        null,
                        0,
                        10
                ).stream()
                .map(EventFullDto::getId)
                .collect(Collectors.toSet());

        assertTrue(collection.containsAll(List.of(eventFullDto1.getId(), eventFullDto3.getId())));

        collection = eventService.getFilteredEventsInternal(
                        List.of(userDto2.getId()),
                        null,
                        null,
                        null,
                        null,
                        0,
                        10
                ).stream()
                .map(EventFullDto::getId)
                .collect(Collectors.toSet());

        assertTrue(collection.contains(eventFullDto2.getId()));

        collection = eventService.getFilteredEventsInternal(
                        null,
                        null,
                        List.of(categoryDto.getId()),
                        null,
                        null,
                        0,
                        10
                ).stream()
                .map(EventFullDto::getId)
                .collect(Collectors.toSet());

        assertTrue(collection.containsAll(List.of(eventFullDto1.getId(), eventFullDto2.getId(), eventFullDto3.getId())));

        collection = eventService.getFilteredEventsInternal(
                        null,
                        null,
                        null,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(1),
                        0,
                        10
                ).stream()
                .map(EventFullDto::getId)
                .collect(Collectors.toSet());

        assertTrue(collection.containsAll(List.of(eventFullDto1.getId(), eventFullDto2.getId())));
    }

    @Test
    public void updateEventTest() {
        UserDto userDto = userService.addUser(makeDefaultUser());
        CategoryDto categoryDto = categoryService.addCategory(makeNewCategoryRequest());
        EventFullDto eventFullDto = eventService.addEvent(makeNewEventRequest(categoryDto.getId()), userDto.getId());
        EventFullDto savedEvent = eventService.getEvent(eventFullDto.getId(), "0.0.0.0");

        eventService.updateEvent(UpdateEventRequest.builder()
                .eventId(savedEvent.getId())
                .title("updated title")
                .build(), userDto.getId());

        EventFullDto updatedEvent = eventService.getEvent(savedEvent.getId(), "0.0.0.0");

        savedEvent.setTitle("updated title");
        assertEquals(savedEvent, updatedEvent);
    }

    @Test
    public void updateEventAdminTest() {
        UserDto userDto = userService.addUser(makeDefaultUser());
        CategoryDto categoryDto = categoryService.addCategory(makeNewCategoryRequest());
        EventFullDto eventFullDto = eventService.addEvent(makeNewEventRequest(categoryDto.getId()), userDto.getId());
        EventFullDto savedEvent = eventService.getEvent(eventFullDto.getId(), "0.0.0.0");

        eventService.updateEventAdmin(AdminUpdateEventRequest.builder()
                .title("updated title")
                .build(), eventFullDto.getId());

        EventFullDto updatedEvent = eventService.getEvent(savedEvent.getId(), "0.0.0.0");

        savedEvent.setTitle("updated title");
        assertEquals(savedEvent, updatedEvent);
    }

    @Test
    public void cancelEventTest() {
        UserDto userDto = userService.addUser(makeDefaultUser());
        CategoryDto categoryDto = categoryService.addCategory(makeNewCategoryRequest());
        EventFullDto eventFullDto = eventService.addEvent(makeNewEventRequest(categoryDto.getId()), userDto.getId());
        EventFullDto savedEvent = eventService.getEvent(eventFullDto.getId(), "0.0.0.0");

        eventService.cancelEvent(eventFullDto.getId(), userDto.getId());

        EventFullDto updatedEvent = eventService.getEvent(savedEvent.getId(), "0.0.0.0");

        savedEvent.setState("CANCELED");
        assertEquals(savedEvent, updatedEvent);
    }

    @Test
    public void publishEventTest() {
        UserDto userDto = userService.addUser(makeDefaultUser());
        CategoryDto categoryDto = categoryService.addCategory(makeNewCategoryRequest());
        EventFullDto eventFullDto = eventService.addEvent(makeNewEventRequest(categoryDto.getId()), userDto.getId());
        EventFullDto savedEvent = eventService.getEvent(eventFullDto.getId(), "0.0.0.0");

        eventService.publishEvent(eventFullDto.getId());

        EventFullDto updatedEvent = eventService.getEvent(savedEvent.getId(), "0.0.0.0");

        savedEvent.setState("PUBLISHED");
        savedEvent.setPublishedOn(updatedEvent.getPublishedOn());
        assertEquals(savedEvent, updatedEvent);
    }

    @Test
    public void rejectEventTest() {
        UserDto userDto = userService.addUser(makeDefaultUser());
        CategoryDto categoryDto = categoryService.addCategory(makeNewCategoryRequest());
        EventFullDto eventFullDto = eventService.addEvent(makeNewEventRequest(categoryDto.getId()), userDto.getId());
        EventFullDto savedEvent = eventService.getEvent(eventFullDto.getId(), "0.0.0.0");

        eventService.rejectEvent(eventFullDto.getId());

        EventFullDto updatedEvent = eventService.getEvent(savedEvent.getId(), "0.0.0.0");

        savedEvent.setState("CANCELED");
        assertEquals(savedEvent, updatedEvent);
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
