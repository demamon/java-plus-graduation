package ru.practicum.interaction.api.dto.event;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.interaction.api.dto.category.CategoryDto;
import ru.practicum.interaction.api.dto.event.location.LocationDto;
import ru.practicum.interaction.api.dto.user.UserShortDto;
import ru.practicum.interaction.api.enums.event.EventState;


@Getter
@Setter
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {
    Long id;
    String annotation;
    CategoryDto category;
    Long confirmedRequests;
    String createdOn;
    String description;
    String eventDate;
    UserShortDto initiator;
    LocationDto location;
    Boolean paid;
    Long participantLimit;
    String publishedOn;
    Boolean requestModeration;
    EventState state;
    String title;
}
