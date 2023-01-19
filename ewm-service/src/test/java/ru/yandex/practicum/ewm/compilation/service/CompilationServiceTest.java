package ru.yandex.practicum.ewm.compilation.service;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.ewm.category.dto.CategoryDto;
import ru.yandex.practicum.ewm.category.dto.NewCategoryDto;
import ru.yandex.practicum.ewm.category.service.CategoryService;
import ru.yandex.practicum.ewm.compilation.dto.CompilationDto;
import ru.yandex.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.ewm.event.dto.EventFullDto;
import ru.yandex.practicum.ewm.event.dto.EventShortDto;
import ru.yandex.practicum.ewm.event.dto.LocationDto;
import ru.yandex.practicum.ewm.event.dto.NewEventDto;
import ru.yandex.practicum.ewm.event.service.EventService;
import ru.yandex.practicum.ewm.stats.service.EwmStatsService;
import ru.yandex.practicum.ewm.user.dto.UserDto;
import ru.yandex.practicum.ewm.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AllArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CompilationServiceTest {

    private CompilationService compilationService;
    private EventService eventService;
    private CategoryService categoryService;
    private UserService userService;
    @MockBean
    private EwmStatsService statsService;


    @Test
    public void addCompilationTest() {
        UserDto userDto1 = userService.addUser(makeDefaultUser());
        CategoryDto categoryDto = categoryService.addCategory(makeNewCategoryRequest());
        EventFullDto eventFullDto1 = eventService.addEvent(makeNewEventRequest(categoryDto.getId()), userDto1.getId());
        EventFullDto eventFullDto2 = eventService.addEvent(makeNewEventRequest(categoryDto.getId()), userDto1.getId());
        EventFullDto eventFullDto3 = eventService.addEvent(makeNewEventRequest(categoryDto.getId()), userDto1.getId());

        CompilationDto compilationDto = compilationService.addCompilation(makeNewCompilation(Set.of(
                eventFullDto1.getId(),
                eventFullDto2.getId(),
                eventFullDto3.getId())));

        assertTrue(compilationService.getCompilation(compilationDto.getId()).getEvents().stream()
                .map(EventShortDto::getId).collect(Collectors.toSet())
                .containsAll(Set.of(eventFullDto1.getId(), eventFullDto2.getId(), eventFullDto3.getId())));
    }

    @Test
    public void getCompilationsTest() {
        UserDto userDto1 = userService.addUser(makeDefaultUser());
        CategoryDto categoryDto = categoryService.addCategory(makeNewCategoryRequest());
        EventFullDto eventFullDto1 = eventService.addEvent(makeNewEventRequest(categoryDto.getId()), userDto1.getId());
        EventFullDto eventFullDto2 = eventService.addEvent(makeNewEventRequest(categoryDto.getId()), userDto1.getId());
        EventFullDto eventFullDto3 = eventService.addEvent(makeNewEventRequest(categoryDto.getId()), userDto1.getId());

        compilationService.addCompilation(makeNewCompilation(Set.of(
                eventFullDto1.getId(),
                eventFullDto2.getId(),
                eventFullDto3.getId())));
        compilationService.addCompilation(makeNewCompilation(Set.of(
                eventFullDto1.getId(),
                eventFullDto2.getId())));

        assertEquals(2, compilationService.getCompilations(false, 0, 10).size());
    }

    @Test
    public void removeEventFromCompilationTest() {
        UserDto userDto1 = userService.addUser(makeDefaultUser());
        CategoryDto categoryDto = categoryService.addCategory(makeNewCategoryRequest());
        EventFullDto eventFullDto1 = eventService.addEvent(makeNewEventRequest(categoryDto.getId()), userDto1.getId());
        EventFullDto eventFullDto2 = eventService.addEvent(makeNewEventRequest(categoryDto.getId()), userDto1.getId());
        EventFullDto eventFullDto3 = eventService.addEvent(makeNewEventRequest(categoryDto.getId()), userDto1.getId());

        CompilationDto compilationDto1 = compilationService.addCompilation(makeNewCompilation(Set.of(
                eventFullDto1.getId(),
                eventFullDto2.getId(),
                eventFullDto3.getId())));
        compilationService.removeEventFromCompilation(compilationDto1.getId(), eventFullDto1.getId());

        assertFalse(compilationService.getCompilation(compilationDto1.getId()).getEvents()
                .contains(eventFullDto1.getId()));
    }

    @Test
    public void changePinnedTest() {
        UserDto userDto1 = userService.addUser(makeDefaultUser());
        CategoryDto categoryDto = categoryService.addCategory(makeNewCategoryRequest());
        EventFullDto eventFullDto1 = eventService.addEvent(makeNewEventRequest(categoryDto.getId()), userDto1.getId());
        EventFullDto eventFullDto2 = eventService.addEvent(makeNewEventRequest(categoryDto.getId()), userDto1.getId());
        EventFullDto eventFullDto3 = eventService.addEvent(makeNewEventRequest(categoryDto.getId()), userDto1.getId());

        CompilationDto compilationDto = compilationService.addCompilation(makeNewCompilation(Set.of(
                eventFullDto1.getId(),
                eventFullDto2.getId(),
                eventFullDto3.getId())));
        compilationService.changePinned(compilationDto.getId(), true);

        assertTrue(compilationService.getCompilation(compilationDto.getId()).getPinned());
    }

    @Test
    public void deleteCompilationTest() {
        UserDto userDto1 = userService.addUser(makeDefaultUser());
        CategoryDto categoryDto = categoryService.addCategory(makeNewCategoryRequest());
        EventFullDto eventFullDto1 = eventService.addEvent(makeNewEventRequest(categoryDto.getId()), userDto1.getId());
        EventFullDto eventFullDto2 = eventService.addEvent(makeNewEventRequest(categoryDto.getId()), userDto1.getId());
        EventFullDto eventFullDto3 = eventService.addEvent(makeNewEventRequest(categoryDto.getId()), userDto1.getId());

        CompilationDto compilationDto = compilationService.addCompilation(makeNewCompilation(Set.of(
                eventFullDto1.getId(),
                eventFullDto2.getId(),
                eventFullDto3.getId())));
        compilationService.deleteCompilation(compilationDto.getId());

        assertTrue(compilationService.getCompilations(false, 0, 10).isEmpty());
    }

    private NewCompilationDto makeNewCompilation(Set<Long> events) {
        return NewCompilationDto.builder()
                .pinned(false)
                .events(events)
                .title("new compilation")
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
