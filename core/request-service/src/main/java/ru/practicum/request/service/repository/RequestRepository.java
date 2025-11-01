package ru.practicum.request.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.interaction.api.enums.request.RequestState;
import ru.practicum.request.service.model.Request;

public interface RequestRepository extends JpaRepository<Request, Long>, QuerydslPredicateExecutor<Request> {
    boolean existsByEventIdAndRequesterIdAndState(Long eventId, Long userId, RequestState state);

}
