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
@Table(name = "events_similarity")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventSimilarity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "event_a")
    Long eventA;

    @Column(name = "event_b")
    Long eventB;

    Double score;

    Instant timestamp;
}
