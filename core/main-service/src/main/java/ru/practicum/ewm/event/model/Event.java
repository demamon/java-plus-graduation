package ru.practicum.ewm.event.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.user.User;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String annotation;
    @ManyToOne
    Category category;
    @Column(name = "confirmed_requests")
    Long confirmedRequests;
    @Column(name = "created_on")
    LocalDateTime createdOn;
    String description;
    @Column(name = "event_date")
    LocalDateTime eventDate;
    @ManyToOne
    User initiator;
    @OneToOne
    Location location;
    Boolean paid;
    @Column(name = "participant_limit")
    Long participantLimit;
    @Column(name = "published_on")
    LocalDateTime publishedOn;
    @Column(name = "request_moderation")
    Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    EventState state;
    String title;

    public Event(String annotation,
                 String description,
                 LocalDateTime eventDate,
                 Location location,
                 Boolean paid,
                 Long participantLimit,
                 Boolean requestModeration,
                 String title) {
        this.annotation = annotation;
        this.confirmedRequests = 0L;
        this.createdOn = LocalDateTime.now();
        this.description = description;
        this.eventDate = eventDate;
        this.location = location;
        this.paid = paid;
        this.participantLimit = participantLimit;
        this.publishedOn = eventDate;
        this.requestModeration = requestModeration;
        this.state = EventState.PENDING;
        this.title = title;
    }

    public void increaseCountOfConfirmedRequest() {
        this.confirmedRequests += 1;
    }

    public void decreaseCountOfConfirmedRequest() {
        this.confirmedRequests -= 1;
    }
}