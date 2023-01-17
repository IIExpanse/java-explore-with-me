package ru.yandex.practicum.ewm.category.model;

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
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;
    @Column(name = "category_name")
    private String name;
    @OneToMany(mappedBy = "category")
    @ToString.Exclude
    private Set<Event> events;
}
