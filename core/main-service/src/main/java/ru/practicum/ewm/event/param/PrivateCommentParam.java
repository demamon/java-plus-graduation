package ru.practicum.ewm.event.param;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.event.dto.comment.NewCommentDto;

@Getter
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrivateCommentParam {
    Long userId;
    Long eventId;
    int from;
    int size;
    NewCommentDto newComment;
}
