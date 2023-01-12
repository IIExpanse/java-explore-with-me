package ru.yandex.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.ewm.event.validator.EarliestStartTimeConstraint;
import ru.yandex.practicum.ewm.event.validator.NullOrNotBlankConstraint;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventRequest {

    @NullOrNotBlankConstraint(message = "Новая аннотация не может состоять только из пробелов.")
    private String annotation;
    private Long category;
    @NullOrNotBlankConstraint(message = "Новое описание не может состоять только из пробелов.")
    private String description;
    @Future(message = "Дата и время проведения события должны быть в будущем.")
    @EarliestStartTimeConstraint(days = 0, hours = 2, minutes = 0,
            message = "Время проведения события должно быть не ранее, чем через два часа от настоящего момента.")
    private LocalDateTime eventDate;
    @NotNull(message = "Идентификатор обновляемого события не может быть пустым.")
    private Long eventId;
    private Boolean paid;
    @Positive(message = "Число участников не может быть отрицательным.")
    private Integer participantLimit;
    @NullOrNotBlankConstraint(message = "Новый заголовок не может состоять только из пробелов.")
    private String title;
}
