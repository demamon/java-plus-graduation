package ru.practicum.interaction.api.dto.compilation;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminCompilationParam {
    long compId;
    NewCompilationDto compilationFromRequest;
    UpdateCompilationRequest compilationOnUpdate;
}
