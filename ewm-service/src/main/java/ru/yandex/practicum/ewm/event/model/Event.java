package ru.yandex.practicum.ewm.event.model;

import lombok.*;
import ru.yandex.practicum.ewm.category.model.Category;
import ru.yandex.practicum.ewm.compilation.model.Compilation;
import ru.yandex.practicum.ewm.request.model.ParticipationRequest;
import ru.yandex.practicum.ewm.review.model.Review;
import ru.yandex.practicum.ewm.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;
    @Column(name = "annotation")
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "event_category", referencedColumnName = "category_id")
    private Category category;
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @Column(name = "description")
    private String description;
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = "initiator", referencedColumnName = "user_id")
    private User initiator;
    @Column(name = "lat")
    private Double lat;
    @Column(name = "lon")
    private Double lon;
    @Column(name = "paid")
    private Boolean paid;
    @Column(name = "participation_limit")
    private Integer participantLimit;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private EventState state;
    @Column(name = "title")
    private String title;
    @OneToMany(mappedBy = "event")
    @ToString.Exclude
    private Set<ParticipationRequest> requests;
    @ManyToMany
    @JoinTable(name = "events_compilations",
            joinColumns = @JoinColumn(name = "ref_event", referencedColumnName = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "ref_compilation", referencedColumnName = "compilation_id"))
    @ToString.Exclude
    private Set<Compilation> compilations;

    @OneToMany(mappedBy = "event")
    @ToString.Exclude
    private Set<Review> reviews;
}
