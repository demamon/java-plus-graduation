package ru.practicum.ewm.event.dto.comment;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCommentDto {

    @NotBlank(message = "Описание комментария к событию не может быть пустым")
    @Size(min = 20, max = 7000, message = "Описание комментария к событию не может быть меньше 20 и больше 7000 символов")
    String description;

    @NotNull(message = "Комментарий должен иметь id события")
    @Positive(message = "Id события не может быть равно 0")
    Long event;

    @NotNull(message = "Комментарий должен иметь id пользователя")
    @Positive(message = "Id пользователя не может быть равно 0")
    Long user;
}
