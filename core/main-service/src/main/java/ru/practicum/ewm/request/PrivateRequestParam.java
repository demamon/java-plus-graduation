package ru.practicum.ewm.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrivateRequestParam {
    long userId;
    long eventId;
    long requestId;
}
