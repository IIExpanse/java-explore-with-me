package ru.yandex.practicum.ewm.request.service;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.ewm.category.dto.CategoryDto;
import ru.yandex.practicum.ewm.category.dto.NewCategoryDto;
import ru.yandex.practicum.ewm.category.service.CategoryService;
import ru.yandex.practicum.ewm.event.dto.EventFullDto;
import ru.yandex.practicum.ewm.event.dto.LocationDto;
import ru.yandex.practicum.ewm.event.dto.NewEventDto;
import ru.yandex.practicum.ewm.event.service.EventService;
import ru.yandex.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.yandex.practicum.ewm.request.model.RequestStatus;
import ru.yandex.practicum.ewm.stats.service.EwmStatsService;
import ru.yandex.practicum.ewm.user.dto.UserDto;
import ru.yandex.practicum.ewm.user.service.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AllArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = "classpath:schema-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class RequestServiceTest {

    private UserService userService;
    private CategoryService categoryService;
    private EventService eventService;
    private RequestService requestService;
    @MockBean
    private EwmStatsService statsService;

    @BeforeEach
    public void configureStatsMock() {
        Mockito.when(statsService.getViewsForEvent(Mockito.anyLong()))
                .thenReturn(0);
    }

    @Test
    public void addRequestTest() {
        UserDto userDto1 = userService.addUser(makeDefaultUser());
        UserDto userDto2 = makeDefaultUser();
        userDto2.setEmail("new@mail.ru");
        userDto2 = userService.addUser(userDto2);
        CategoryDto categoryDto = categoryService.addCategory(makeNewCategoryRequest());
        EventFullDto eventFullDto = eventService.addEvent(makeNewEventRequest(categoryDto.getId()), userDto1.getId());
        eventService.publishEvent(eventFullDto.getId());

        ParticipationRequestDto requestDto = requestService.addRequest(userDto2.getId(), eventFullDto.getId());
        requestDto.setCreated(requestDto.getCreated().truncatedTo(ChronoUnit.MILLIS));

        List<ParticipationRequestDto> requests = List.copyOf(requestService.getRequestsByUser(userDto2.getId()));
        requests.get(0).setCreated(requests.get(0).getCreated().truncatedTo(ChronoUnit.MILLIS));

        assertEquals(List.of(requestDto), requests);
    }

    @Test
    public void getRequestsByEventOwnerTest() {
        UserDto userDto1 = userService.addUser(makeDefaultUser());
        UserDto userDto2 = makeDefaultUser();
        userDto2.setEmail("new@mail.ru");
        userDto2 = userService.addUser(userDto2);
        CategoryDto categoryDto = categoryService.addCategory(makeNewCategoryRequest());
        EventFullDto eventFullDto = eventService.addEvent(makeNewEventRequest(categoryDto.getId()), userDto1.getId());
        eventService.publishEvent(eventFullDto.getId());

        ParticipationRequestDto requestDto = requestService.addRequest(userDto2.getId(), eventFullDto.getId());
        requestDto.setCreated(requestDto.getCreated().truncatedTo(ChronoUnit.MILLIS));

        List<ParticipationRequestDto> requests = List.copyOf(requestService.getRequestsByEventOwner(
                userDto1.getId(),
                eventFullDto.getId()));
        requests.get(0).setCreated(requests.get(0).getCreated().truncatedTo(ChronoUnit.MILLIS));

        assertEquals(List.of(requestDto), requests);
    }

    @Test
    public void getConfirmedRequestsCountTest() {
        UserDto userDto1 = userService.addUser(makeDefaultUser());
        UserDto userDto2 = makeDefaultUser();
        userDto2.setEmail("new@mail.ru");
        userDto2 = userService.addUser(userDto2);
        CategoryDto categoryDto = categoryService.addCategory(makeNewCategoryRequest());

        NewEventDto newEventDto = makeNewEventRequest(categoryDto.getId());
        newEventDto.setRequestModeration(true);
        newEventDto.setParticipantLimit(1000);
        EventFullDto eventFullDto = eventService.addEvent(newEventDto, userDto1.getId());
        eventService.publishEvent(eventFullDto.getId());

        ParticipationRequestDto requestDto = requestService.addRequest(userDto2.getId(), eventFullDto.getId());
        requestService.confirmRequestInOwnerEvent(userDto1.getId(), eventFullDto.getId(), requestDto.getId());

        assertEquals(1, requestService.getConfirmedRequestsCount(eventFullDto.getId()));
    }

    @Test
    public void cancelRequestTest() {
        UserDto userDto1 = userService.addUser(makeDefaultUser());
        UserDto userDto2 = makeDefaultUser();
        userDto2.setEmail("new@mail.ru");
        userDto2 = userService.addUser(userDto2);
        CategoryDto categoryDto = categoryService.addCategory(makeNewCategoryRequest());

        NewEventDto newEventDto = makeNewEventRequest(categoryDto.getId());
        newEventDto.setRequestModeration(true);
        newEventDto.setParticipantLimit(1000);
        EventFullDto eventFullDto = eventService.addEvent(newEventDto, userDto1.getId());
        eventService.publishEvent(eventFullDto.getId());

        ParticipationRequestDto requestDto = requestService.addRequest(userDto2.getId(), eventFullDto.getId());
        requestService.cancelRequest(userDto2.getId(), requestDto.getId());

        assertEquals(RequestStatus.CANCELED.toString(),
                List.copyOf(requestService.getRequestsByUser(userDto2.getId())).get(0).getStatus());
    }

    @Test
    public void confirmRequestInOwnerEventTest() {
        UserDto userDto1 = userService.addUser(makeDefaultUser());
        UserDto userDto2 = makeDefaultUser();
        userDto2.setEmail("new@mail.ru");
        userDto2 = userService.addUser(userDto2);
        CategoryDto categoryDto = categoryService.addCategory(makeNewCategoryRequest());

        NewEventDto newEventDto = makeNewEventRequest(categoryDto.getId());
        newEventDto.setRequestModeration(true);
        newEventDto.setParticipantLimit(1000);
        EventFullDto eventFullDto = eventService.addEvent(newEventDto, userDto1.getId());
        eventService.publishEvent(eventFullDto.getId());

        ParticipationRequestDto requestDto = requestService.addRequest(userDto2.getId(), eventFullDto.getId());
        requestService.confirmRequestInOwnerEvent(userDto1.getId(), eventFullDto.getId(), requestDto.getId());

        assertEquals(RequestStatus.CONFIRMED.toString(),
                List.copyOf(requestService.getRequestsByUser(userDto2.getId())).get(0).getStatus());
    }

    @Test
    public void rejectRequestInOwnerEventTest() {
        UserDto userDto1 = userService.addUser(makeDefaultUser());
        UserDto userDto2 = makeDefaultUser();
        userDto2.setEmail("new@mail.ru");
        userDto2 = userService.addUser(userDto2);
        CategoryDto categoryDto = categoryService.addCategory(makeNewCategoryRequest());

        NewEventDto newEventDto = makeNewEventRequest(categoryDto.getId());
        newEventDto.setRequestModeration(true);
        newEventDto.setParticipantLimit(1000);
        EventFullDto eventFullDto = eventService.addEvent(newEventDto, userDto1.getId());
        eventService.publishEvent(eventFullDto.getId());

        ParticipationRequestDto requestDto = requestService.addRequest(userDto2.getId(), eventFullDto.getId());
        requestService.rejectRequestInOwnerEvent(userDto1.getId(), eventFullDto.getId(), requestDto.getId());

        assertEquals(RequestStatus.REJECTED.toString(),
                List.copyOf(requestService.getRequestsByUser(userDto2.getId())).get(0).getStatus());
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
