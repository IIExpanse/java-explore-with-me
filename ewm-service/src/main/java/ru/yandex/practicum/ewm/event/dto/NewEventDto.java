package ru.yandex.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.ewm.event.validator.EarliestStartTimeConstraint;

import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {

    @NotBlank(message = "Аннотация не может быть пустой.")
    private String annotation;
    @NotNull(message = "Категория не может быть пустой.")
    private Long category;
    @NotBlank(message = "Описание не может пустым.")
    private String description;
    @NotNull(message = "Дата события не может быть пустым.")
    @Future(message = "Дата и время проведения события должны быть в будущем.")
    @EarliestStartTimeConstraint(days = 0, hours = 2, minutes = 0,
            message = "Время проведения события должно быть не ранее, чем через два часа от настоящего момента.")
    private LocalDateTime eventDate;
    @NotNull(message = "Место проведения не может быть пустым.")
    @Valid
    private LocationDto location;
    private Boolean paid;
    @Positive(message = "Количество участников не может быть отрицательным.")
    private Integer participantLimit;
    private Boolean requestModeration;
    @NotBlank(message = "Заголовок не может быть пустым.")
    private String title;
}
