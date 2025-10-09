package ru.practicum.ewm.event.dto.comment;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCommentDto {

    @Size(min = 20, max = 7000, message = "Описание комментария к событию не может быть меньше 20 и больше 7000 символов")
    String description;

    public boolean hasDescription() {
        return description != null && !description.isBlank();
    }
}
