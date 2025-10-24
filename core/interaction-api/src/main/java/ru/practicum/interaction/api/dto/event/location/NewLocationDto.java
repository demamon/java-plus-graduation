package ru.practicum.interaction.api.dto.event.location;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class NewLocationDto {
    @NotNull(message = "Широта не может быть пустой")
    Double lat;
    @NotNull(message = "Долгота не может быть пустой")
    Double lon;
}
