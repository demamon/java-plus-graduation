package ru.practicum.ewm.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminUserParam {
    List<Long> ids;
    int from;
    int size;
}
