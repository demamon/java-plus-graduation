package ru.practicum.event.service.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.event.service.event.model.Event;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    Optional<Event> findByIdAndInitiatorId(long eventId, long userId);

    boolean existsByCategoryId(long categoryId);
}
