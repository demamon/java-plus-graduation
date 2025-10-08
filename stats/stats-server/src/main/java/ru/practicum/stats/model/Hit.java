package ru.practicum.stats.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "hits")
@Data
@EqualsAndHashCode(of = "ip")
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Hit {

    public Hit(String app, String uri, String ip, LocalDateTime timestamp) {
        this.app = app;
        this.uri = uri;
        this.ip = ip;
        this.timestamp = timestamp;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String app;

    String uri;

    String ip;

    @Column(name = "time_request")
    LocalDateTime timestamp;
}
