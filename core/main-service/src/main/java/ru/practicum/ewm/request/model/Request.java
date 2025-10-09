package ru.practicum.ewm.request.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne
    Event event;
    @ManyToOne
    User requester;
    @Enumerated(EnumType.STRING)
    RequestState state;

    LocalDateTime created;

    public Request(Event event, User requester) {
        this.event = event;
        this.requester = requester;
        this.created = LocalDateTime.now();
    }
}