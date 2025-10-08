package ru.practicum.ewm.event.dto;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.event.dto.location.NewLocationDto;

@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {

    @NotBlank(message = "Аннотация не может быть пустой")
    @Size(min = 20, max = 2000, message = "Аннотация к событию не может быть меньше 20 и больше 2000 символов")
    String annotation;

    @NotNull(message = "Событие должно иметь id категории")
    @Positive(message = "Id события не может быть равно 0")
    Long category;

    @NotBlank(message = "Описание не может быть пустым")
    @Size(min = 20, max = 7000, message = "Описание к событию не может быть меньше 20 и больше 7000 символов")
    String description;

    @NotNull(message = "Дата события не может быть пустой")
    String eventDate;

    @NotNull(message = "Объект локации не может быть пустым")
    NewLocationDto location;

    Boolean paid;

    @PositiveOrZero
    Long participantLimit;

    Boolean requestModeration;

    @NotBlank(message = "Название не может быть пустым")
    @Size(min = 3, max = 120, message = "Название не может быть меньше 3 и больше 120 символов")
    String title;
}
