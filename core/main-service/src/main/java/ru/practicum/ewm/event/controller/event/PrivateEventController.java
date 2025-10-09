package ru.practicum.ewm.event.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.param.PrivateEventParam;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class PrivateEventController {

    private final EventService eventService;
    private final String eventsPath = "/{user-id}/events";
    private final String eventPath = "/{user-id}/events/{event-id}";
    private final String requestsPath = "/{user-id}/events/{event-id}/requests";

    @GetMapping(eventsPath)
    public List<EventFullDto> getEventsOfUser(@PathVariable(name = "user-id") long userId,
                                              @RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "10") int size) {
        PrivateEventParam param = PrivateEventParam.builder()
                .userId(userId)
                .from(from)
                .size(size)
                .build();
        return eventService.getEventsOfUser(param);
    }

    @GetMapping(eventPath)
    public EventFullDto getEventOfUser(@PathVariable(name = "user-id") long userId,
                                       @PathVariable(name = "event-id") long eventId) {
        PrivateEventParam param = PrivateEventParam.builder()
                .userId(userId)
                .eventId(eventId)
                .build();
        return eventService.getEventOfUser(param);
    }

    @PostMapping(eventsPath)
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@Valid @RequestBody NewEventDto event,
                                    @PathVariable(name = "user-id") long userId) {
        log.debug("получаем запрос на создание события");
        PrivateEventParam param = PrivateEventParam.builder()
                .newEvent(event)
                .userId(userId)
                .build();
        return eventService.createEvent(param);
    }

    @PatchMapping(eventPath)
    public EventFullDto updateEvent(@Valid @RequestBody UpdateEventUserRequest event,
                                    @PathVariable(name = "user-id") long userId,
                                    @PathVariable(name = "event-id") long eventId) {
        PrivateEventParam param = PrivateEventParam.builder()
                .eventOnUpdate(event)
                .userId(userId)
                .eventId(eventId)
                .build();
        return eventService.updateEvent(param);
    }

    @GetMapping(requestsPath)
    public List<ParticipationRequestDto> getRequestsOfUser(@PathVariable(name = "user-id") long userId,
                                                           @PathVariable(name = "event-id") long eventId) {
        PrivateEventParam param = PrivateEventParam.builder()
                .userId(userId)
                .eventId(eventId)
                .build();
        return eventService.getRequestsOfUser(param);
    }

    @PatchMapping(requestsPath)
    public EventRequestStatusUpdateResult updateStatusOfRequests(@RequestBody EventRequestStatusUpdateRequest request,
                                                                 @PathVariable(name = "user-id") long userId,
                                                                 @PathVariable(name = "event-id") long eventId) {
        PrivateEventParam param = PrivateEventParam.builder()
                .request(request)
                .userId(userId)
                .eventId(eventId)
                .build();
        return eventService.updateStatusOfRequests(param);
    }
}