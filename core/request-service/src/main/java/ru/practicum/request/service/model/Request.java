package ru.practicum.request.service.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.interaction.api.enums.request.RequestState;

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
    Long eventId;
    Long requesterId;
    @Enumerated(EnumType.STRING)
    RequestState state;

    LocalDateTime created;

    public Request(Long eventId, Long requesterId) {
        this.eventId = eventId;
        this.requesterId = requesterId;
        this.created = LocalDateTime.now();
    }
}