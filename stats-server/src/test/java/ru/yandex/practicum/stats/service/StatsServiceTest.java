package ru.yandex.practicum.stats.service;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.stats.dto.EndpointHitDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AllArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class StatsServiceTest {

    private StatsService service;

    @Test
    public void saveHitTest() {
        EndpointHitDto endpointHitDto = service.saveHit(makeNewHit());
        assertEquals(1L, endpointHitDto.getId());
    }

    @Test
    public void getHitsTest() {
        EndpointHitDto endpointHitDto1 = service.saveHit(makeNewHit());
        service.saveHit(makeNewHit());

        assertEquals(2, List.copyOf(service.getHits(
                        LocalDateTime.now().minusDays(1),
                        LocalDateTime.now().plusDays(1),
                        List.of(endpointHitDto1.getUri()),
                        false))
                .get(0).getHits());

        assertEquals(1, List.copyOf(service.getHits(
                        LocalDateTime.now().minusDays(1),
                        LocalDateTime.now().plusDays(1),
                        List.of(endpointHitDto1.getUri()),
                        true))
                .get(0).getHits());
    }

    private EndpointHitDto makeNewHit() {
        return EndpointHitDto.builder()
                .app("ewm-main-service")
                .uri("/event/1")
                .ip("0.0.0.0")
                .time(LocalDateTime.now())
                .build();
    }
}
