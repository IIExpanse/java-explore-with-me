package ru.yandex.practicum.ewm.user.model;

import lombok.*;
import org.hibernate.Hibernate;
import ru.yandex.practicum.ewm.event.model.Event;
import ru.yandex.practicum.ewm.request.model.ParticipationRequest;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Column(name = "email")
    private String email;
    @Column(name = "user_name")
    private String name;
    @OneToMany(mappedBy = "initiator")
    @ToString.Exclude
    private Set<Event> events;
    @OneToMany(mappedBy = "requester")
    @ToString.Exclude
    private Set<ParticipationRequest> requests;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
