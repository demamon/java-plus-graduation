package ru.practicum.event.service.event.service;

import ru.practicum.interaction.api.dto.event.EventFilter;
import ru.practicum.interaction.api.dto.event.EventFullDto;
import ru.practicum.interaction.api.dto.event.EventShortDto;
import ru.practicum.interaction.api.dto.event.UpdateEventAdminRequest;
import ru.practicum.interaction.api.dto.event.PrivateEventParam;
import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.interaction.api.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.interaction.api.dto.request.ParticipationRequestDto;

import java.util.List;


import java.util.Collection;

public interface EventService {

    List<EventFullDto> getEventsOfUser(PrivateEventParam param);

    EventFullDto getEventOfUser(PrivateEventParam param);

    EventFullDto createEvent(PrivateEventParam param);

    EventFullDto updateEvent(PrivateEventParam param);

    List<ParticipationRequestDto> getRequestsOfUser(PrivateEventParam param);

    EventRequestStatusUpdateResult updateStatusOfRequests(PrivateEventParam param);

    Collection<EventShortDto> getPublicAllEvents(EventFilter filter, HttpServletRequest request);

    EventFullDto getPublicEvent(Long eventId, Long userId);

    Collection<EventFullDto> getAdminAllEvents(EventFilter filter);

    EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest updateEvent);

    EventFullDto getEvent(Long eventId);

    void increaseCountOfConfirmedRequest(Long eventId);

    void decreaseCountOfConfirmedRequest(Long eventId);

    List<EventShortDto> getEventsRecommendations(Long userId, Integer maxResults);

    void addLikeToEvent(Long eventId, Long userId);
}
