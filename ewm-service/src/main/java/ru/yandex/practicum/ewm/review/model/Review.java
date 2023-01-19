package ru.yandex.practicum.ewm.review.model;

import lombok.*;
import org.hibernate.Hibernate;
import ru.yandex.practicum.ewm.event.model.Event;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Review review = (Review) o;
        return id != null && Objects.equals(id, review.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
