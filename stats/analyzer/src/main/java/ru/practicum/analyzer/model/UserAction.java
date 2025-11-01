package ru.practicum.analyzer.model;

import lombok.*;
import jakarta.persistence.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_actions")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "event_id")
    Long eventId;

    @Column(name = "user_id")
    Long userId;

    Float mark;

    Instant timestamp;
}
