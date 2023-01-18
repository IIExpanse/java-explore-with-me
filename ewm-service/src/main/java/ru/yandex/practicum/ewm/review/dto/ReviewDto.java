package ru.yandex.practicum.ewm.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDto {

    private Long id;
    private String reviewerName;
    private String text;
    private LocalDateTime created;
    private Long eventId;
}
