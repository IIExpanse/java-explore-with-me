package ru.yandex.practicum.ewm.review.model;

import lombok.*;
import ru.yandex.practicum.ewm.event.model.Event;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "moderator_reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;
    @Column(name = "reviewer_name")
    private String reviewerName;
    @Column(name = "text")
    private String text;
    @Column(name = "created_on")
    private LocalDateTime created;
    @ManyToOne
    @JoinColumn(name = "reviewed_event", referencedColumnName = "event_id")
    private Event event;
}
