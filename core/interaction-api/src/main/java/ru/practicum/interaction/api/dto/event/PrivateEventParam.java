package ru.practicum.interaction.api.dto.event;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.interaction.api.dto.request.EventRequestStatusUpdateRequest;

@Getter
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrivateEventParam {
    Long userId;
    int from;
    int size;
    Long eventId;
    NewEventDto newEvent;
    UpdateEventUserRequest eventOnUpdate;
    EventRequestStatusUpdateRequest request;
}
