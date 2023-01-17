package ru.yandex.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.ewm.event.model.Event;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {

    public LocationDto(Event event) {
        this.lat = event.getLat();
        this.lon = event.getLon();
    }

    @NotNull(message = "Широта не может быть пустой.")
    private Double lat;
    @NotNull(message = "Долгота не может быть пустой.")
    private Double lon;
}
