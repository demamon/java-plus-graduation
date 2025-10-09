package ru.practicum.ewm.compilation.param;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublicCompilationParam {
    long compId;
    Boolean pinned;
    int from;
    int size;
}
