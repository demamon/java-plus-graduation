package ru.practicum.ewm.event.param;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OpenCommentParam {
    Long eventId;
    int from;
    int size;
}
