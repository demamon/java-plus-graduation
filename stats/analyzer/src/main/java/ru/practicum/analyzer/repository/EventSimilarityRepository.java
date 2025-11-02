package ru.practicum.analyzer.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import ru.practicum.analyzer.model.EventSimilarity;

import java.util.List;
import java.util.Set;

public interface EventSimilarityRepository extends JpaRepository<EventSimilarity, Long> {

    List<EventSimilarity> findAllByEventAIn(Set<Long> eventIds, PageRequest pageRequest);

    List<EventSimilarity> findAllByEventBIn(Set<Long> eventIds, PageRequest pageRequest);

    List<EventSimilarity> findAllByEventA(Long eventId, PageRequest pageRequest);

    List<EventSimilarity> findAllByEventB(Long eventId, PageRequest pageRequest);

    boolean existsByEventAAndEventB(Long eventA, Long eventB);

    EventSimilarity findByEventAAndEventB(Long eventA, Long eventB);

    List<EventSimilarity> findAllByEventAAndEventBIn(
            @Param("eventA") Long eventA,
            @Param("eventB") Set<Long> eventB,
            Pageable pageable);

    List<EventSimilarity> findAllByEventBAndEventAIn(
            @Param("eventB") Long eventB,
            @Param("eventA") Set<Long> eventA,
            Pageable pageable);
}

