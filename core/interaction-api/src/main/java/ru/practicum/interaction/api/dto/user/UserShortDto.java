package ru.practicum.interaction.api.dto.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserShortDto {
    Long id;
    String name;
}
