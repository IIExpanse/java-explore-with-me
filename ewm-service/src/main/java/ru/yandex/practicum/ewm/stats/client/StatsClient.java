package ru.yandex.practicum.ewm.stats.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.yandex.practicum.ewm.serializer.LocalDateTimeCompositeSerializer;
import ru.yandex.practicum.ewm.stats.dto.EndpointHitDto;
import ru.yandex.practicum.ewm.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class StatsClient {

    private static final String API_SAVE_PATH = "/hit";
    private static final String API_GET_PATH = "/stats";

    protected final RestTemplate rest;

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        this.rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public void saveHit(EndpointHitDto endpointHitDto) {
        rest.exchange(
                API_SAVE_PATH,
                HttpMethod.POST,
                new HttpEntity<>(endpointHitDto, defaultHeaders()),
                EndpointHitDto.class);
    }

    public List<ViewStats> getHits(
            LocalDateTime start,
            LocalDateTime end,
            Collection<String> uris,
            Boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(LocalDateTimeCompositeSerializer.DATE_TIME_FORMAT);

        Map<String, String> parameters = Map.of(
                "start", start.format(formatter),
                "end", end.format(formatter),
                "uris", uris.toString(),
                "unique", unique.toString());

        ResponseEntity<List<ViewStats>> response = rest.exchange(
                API_GET_PATH + "?start={start}&end={end}&uris={uris}&unique={unique}",
                HttpMethod.GET,
                new HttpEntity<>(defaultHeaders()),
                new ParameterizedTypeReference<>() {},
                parameters
        );
        return response.getBody();
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        return headers;
    }
}
