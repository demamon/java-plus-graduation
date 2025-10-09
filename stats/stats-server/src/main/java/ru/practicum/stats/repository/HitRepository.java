package ru.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<Hit, Integer>, QuerydslPredicateExecutor<Hit> {

    @Query("SELECT COUNT(h.uri) " +
            "FROM Hit h " +
            "WHERE h.app=?1 AND h.uri=?2")
    Integer findCountViews(String app, String uri);

    @Query("SELECT h " +
            "FROM Hit h " +
            "WHERE h.timestamp BETWEEN ?1 AND ?2 " +
            "AND h.uri IN ?3")
    List<Hit> findAllHitsWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT h " +
            "FROM Hit h " +
            "WHERE h.timestamp BETWEEN ?1 AND ?2")
    List<Hit> findAllHitsWithoutUris(LocalDateTime start, LocalDateTime end);
}
