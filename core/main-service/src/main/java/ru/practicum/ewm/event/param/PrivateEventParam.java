package ru.practicum.ewm.event.param;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;

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
