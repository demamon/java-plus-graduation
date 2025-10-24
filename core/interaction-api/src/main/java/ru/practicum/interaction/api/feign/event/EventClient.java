package ru.practicum.interaction.api.feign.event;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.practicum.interaction.api.dto.event.EventFullDto;

@FeignClient(name = "event-service", path = "/admin/events")
public interface EventClient {
    @GetMapping("/{eventId}")
    EventFullDto getEvent(@PathVariable Long eventId);

    @PostMapping("/{eventId}/confirm-increase")
    void increaseCountOfConfirmedRequest(@PathVariable Long eventId);

    @PostMapping("/{eventId}/confirm-decrease")
    void decreaseCountOfConfirmedRequest(@PathVariable Long eventId);
}
