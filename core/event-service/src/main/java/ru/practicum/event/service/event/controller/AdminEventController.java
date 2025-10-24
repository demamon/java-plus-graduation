package ru.practicum.event.service.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interaction.api.dto.event.EventFilter;
import ru.practicum.interaction.api.dto.event.EventFullDto;
import ru.practicum.interaction.api.dto.event.UpdateEventAdminRequest;
import ru.practicum.event.service.event.service.EventService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@Slf4j
@RequiredArgsConstructor
public class AdminEventController {
    private final EventService eventService;

    @GetMapping
    public Collection<EventFullDto> getAdminAllEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @Future LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        EventFilter eventFilter = EventFilter.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();
        return eventService.getAdminAllEvents(eventFilter);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateByAdmin(@PathVariable Long eventId, @Valid @RequestBody UpdateEventAdminRequest updateEvent) {
        return eventService.updateByAdmin(eventId, updateEvent);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PathVariable Long eventId) {
        return eventService.getEvent(eventId);
    }

    @PostMapping("/{eventId}/confirm-increase")
    public void increaseCountOfConfirmedRequest(@PathVariable Long eventId) {
        eventService.increaseCountOfConfirmedRequest(eventId);
    }

    @PostMapping("/{eventId}/confirm-decrease")
    public void decreaseCountOfConfirmedRequest(@PathVariable Long eventId) {
        eventService.decreaseCountOfConfirmedRequest(eventId);
    }
}
