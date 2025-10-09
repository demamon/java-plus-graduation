package ru.practicum.ewm.event.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.event.dto.location.NewLocationDto;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class UpdateEventUserRequest {

    @Size(min = 20, max = 2000, message = "Аннотация к событию не может быть меньше 20 и больше 2000 символов")
    String annotation;

    Long category;

    @Size(min = 20, max = 7000, message = "Описание к событию не может быть меньше 20 и больше 7000 символов")
    String description;

    String eventDate;

    NewLocationDto location;

    Boolean paid;

    @Positive
    Long participantLimit;

    Boolean requestModeration;

    String stateAction;

    @Size(min = 3, max = 120, message = "Название не может быть меньше 3 и больше 120 символов")
    String title;

    public boolean hasAnnotation() {
        return annotation != null && !annotation.isBlank();
    }

    public boolean hasCategory() {
        return category != null && category != 0L;
    }

    public boolean hasDescription() {
        return description != null && !description.isBlank();
    }

    public boolean hasEventDate() {
        return eventDate != null;
    }

    public boolean hasLocation() {
        return location != null;
    }

    public boolean hasPaid() {
        return paid != null;
    }

    public boolean hasParticipantLimit() {
        return participantLimit != null;
    }

    public boolean hasRequestModeration() {
        return requestModeration != null;
    }

    public boolean hasStateAction() {
        return stateAction != null && !stateAction.isBlank();
    }

    public boolean hasTitle() {
        return title != null && !title.isBlank();
    }
}
