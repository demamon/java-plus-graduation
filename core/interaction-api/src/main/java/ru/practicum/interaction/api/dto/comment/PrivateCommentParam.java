package ru.practicum.interaction.api.dto.comment;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

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
