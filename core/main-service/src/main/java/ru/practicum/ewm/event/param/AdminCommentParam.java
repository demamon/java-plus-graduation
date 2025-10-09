package ru.practicum.ewm.event.param;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.event.dto.comment.UpdateCommentDto;

@Getter
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminCommentParam {
    Long eventId;
    Long commentId;
    UpdateCommentDto comment;
}
