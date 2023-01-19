package ru.yandex.practicum.ewm.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewReviewDto {

    @NotBlank(message = "Имя модератора не может быть пустым.")
    private String reviewerName;
    @NotBlank(message = "Текст комментария не может быть пустым.")
    private String text;
    @NotNull(message = "Идентификатор комментируемого события не может быть пустым.")
    private Long eventId;
}
