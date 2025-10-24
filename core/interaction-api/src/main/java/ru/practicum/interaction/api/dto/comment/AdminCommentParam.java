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
public class AdminCommentParam {
    Long eventId;
    Long commentId;
    UpdateCommentDto comment;
}
