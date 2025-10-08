package ru.practicum.ewm.event.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.event.dto.location.LocationDto;
import ru.practicum.ewm.user.dto.UserShortDto;

@Getter
@Setter
@Builder
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
    String state;
    String title;
    Integer views;
}
