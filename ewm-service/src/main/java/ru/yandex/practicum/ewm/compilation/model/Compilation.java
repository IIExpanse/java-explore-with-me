package ru.yandex.practicum.ewm.compilation.model;

import lombok.*;
import ru.yandex.practicum.ewm.event.model.Event;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "compilations")
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compilation_id")
    private Long id;
    @Column(name = "pinned")
    private Boolean pinned;
    @Column(name = "title")
    private String title;
    @ManyToMany(mappedBy = "compilations")
    @ToString.Exclude
    private Set<Event> events;
}
