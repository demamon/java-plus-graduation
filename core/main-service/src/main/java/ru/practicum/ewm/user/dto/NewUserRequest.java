package ru.practicum.ewm.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewUserRequest {

    @NotBlank(message = "Имейл не может быть пустым")
    @Size(min = 6, max = 254, message = "Имейл не может быть меньше 6 символов и больше 254 символов")
    @Email(message = "Имейл имеет не корректный формат")
    String email;

    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 250, message = "Имя не может быть меньше 2 символов и больше 250 символов")
    String name;
}
