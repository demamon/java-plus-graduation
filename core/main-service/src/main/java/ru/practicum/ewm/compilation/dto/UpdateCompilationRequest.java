package ru.practicum.ewm.compilation.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCompilationRequest {
    Set<Long> events;
    Boolean pinned;
    String title;

    public boolean hasEvents() {
        return events != null && !events.isEmpty();
    }

    public boolean hasPinned() {
        return pinned != null;
    }

    public boolean hasTitle() {
        return title != null && !title.isBlank();
    }
}
