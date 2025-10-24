package ru.practicum.event.service.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.event.service.event.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
