package ru.practicum.interaction.api.dto.event;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.interaction.api.dto.category.CategoryDto;
import ru.practicum.interaction.api.dto.user.UserShortDto;


@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDto {
    Long id;
    String annotation;
    CategoryDto category;
    Long confirmedRequests;
    String eventDate;
    UserShortDto initiator;
    Boolean paid;
    String title;
    Double rating;
}