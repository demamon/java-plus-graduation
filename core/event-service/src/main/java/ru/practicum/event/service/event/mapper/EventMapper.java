package ru.practicum.event.service.event.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.event.service.category.mapper.CategoryMapper;
import ru.practicum.interaction.api.dto.event.*;
import ru.practicum.interaction.api.dto.event.location.LocationDto;
import ru.practicum.interaction.api.dto.event.location.NewLocationDto;
import ru.practicum.event.service.event.model.Event;
import ru.practicum.interaction.api.enums.event.EventState;
import ru.practicum.event.service.event.model.Location;
import ru.practicum.event.service.exception.ValidationException;
import ru.practicum.interaction.api.feign.user.UserClient;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventMapper {
    private static String datePattern = "yyyy-MM-dd HH:mm:ss";
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);

    private final UserClient userClient;

    public EventFullDto mapToEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapToCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn().format(formatter))
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(formatter))
                .initiator(userClient.getUserShort(event.getInitiatorId()))
                .location(mapToLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn().format(formatter))
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .build();
    }

    public List<EventFullDto> mapToEventFullDtoList(Iterable<Event> events) {
        List<EventFullDto> eventsResult = new ArrayList<>();

        for (Event event : events) {
            eventsResult.add(mapToEventFullDto(event));
        }

        return eventsResult;
    }

    public EventShortDto mapToEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapToCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate().format(formatter))
                .initiator(userClient.getUserShort(event.getInitiatorId()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .build();
    }

    public List<EventShortDto> mapToEventShortDto(Iterable<Event> events) {
        List<EventShortDto> eventsResult = new ArrayList<>();

        for (Event event : events) {
            eventsResult.add(mapToEventShortDto(event));
        }

        return eventsResult;
    }

    public Location mapFromRequest(NewLocationDto location) {
        return new Location(
                location.getLat(),
                location.getLon()
        );
    }

    public LocationDto mapToLocationDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }

    public Event mapFromRequest(NewEventDto event) {
        return new Event(
                event.getAnnotation(),
                event.getDescription(),
                LocalDateTime.parse(event.getEventDate(), formatter),
                mapFromRequest(event.getLocation()),
                validatePaid(event.getPaid()),
                validateParticipantLimit(event.getParticipantLimit()),
                validateRequestModeration(event.getRequestModeration()),
                event.getTitle()
        );
    }

    private Boolean validatePaid(Boolean paid) {
        if (paid == null) {
            paid = false;
        }

        return paid;
    }

    private Long validateParticipantLimit(Long participantLimit) {
        if (participantLimit == null) {
            participantLimit = 0L;
        }

        return participantLimit;
    }

    private Boolean validateRequestModeration(Boolean requestModeration) {
        if (requestModeration == null) {
            requestModeration = true;
        }

        return requestModeration;
    }

    public Event updatePrivateEventFields(Event event, UpdateEventUserRequest eventFromRequest) {
        if (eventFromRequest.hasAnnotation()) {
            event.setAnnotation(eventFromRequest.getAnnotation());
        }

        if (eventFromRequest.hasDescription()) {
            event.setDescription(eventFromRequest.getDescription());
        }

        if (eventFromRequest.hasEventDate()) {
            event.setEventDate(LocalDateTime.parse(eventFromRequest.getEventDate(), formatter));
        }

        if (eventFromRequest.hasPaid()) {
            event.setPaid(eventFromRequest.getPaid());
        }

        if (eventFromRequest.hasParticipantLimit()) {
            if (eventFromRequest.getParticipantLimit() < 0)
                throw new ValidationException("Лимит участников не может быть отрицательным");
            event.setParticipantLimit(eventFromRequest.getParticipantLimit());
        }

        if (eventFromRequest.hasRequestModeration()) {
            event.setRequestModeration(eventFromRequest.getRequestModeration());
        }

        if (eventFromRequest.hasStateAction()) {
            if (eventFromRequest.getStateAction().equalsIgnoreCase("SEND_TO_REVIEW")
                    && event.getState().equals(EventState.CANCELED))
                event.setState(EventState.PENDING);
            else if (eventFromRequest.getStateAction().equalsIgnoreCase("CANCEL_REVIEW"))
                event.setState(EventState.CANCELED);
        }

        if (eventFromRequest.hasTitle()) {
            event.setTitle(eventFromRequest.getTitle());
        }

        return event;
    }

    public Event updateAdminEventFields(Event event, UpdateEventAdminRequest eventFromRequest) {
        if (eventFromRequest.hasAnnotation()) {
            event.setAnnotation(eventFromRequest.getAnnotation());
        }

        if (eventFromRequest.hasDescription()) {
            event.setDescription(eventFromRequest.getDescription());
        }

        if (eventFromRequest.hasEventDate()) {
            event.setEventDate(LocalDateTime.parse(eventFromRequest.getEventDate(), formatter));
        }

        if (eventFromRequest.hasPaid()) {
            event.setPaid(eventFromRequest.getPaid());
        }

        if (eventFromRequest.hasParticipantLimit()) {
            if (eventFromRequest.getParticipantLimit() < 0)
                throw new ValidationException("Лимит участников не может быть отрицательным");
            event.setParticipantLimit(eventFromRequest.getParticipantLimit());
        }

        if (eventFromRequest.hasRequestModeration()) {
            event.setRequestModeration(eventFromRequest.getRequestModeration());
        }

        if (eventFromRequest.hasStateAction()) {
            if (eventFromRequest.getStateAction().equalsIgnoreCase("PUBLISH_EVENT"))
                event.setState(EventState.PUBLISHED);
            else if (eventFromRequest.getStateAction().equalsIgnoreCase("REJECT_EVENT"))
                event.setState(EventState.CANCELED);
        }

        if (eventFromRequest.hasTitle()) {
            event.setTitle(eventFromRequest.getTitle());
        }

        return event;
    }
}