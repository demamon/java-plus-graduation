package ru.practicum.ewm.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCategoryRequest {
    @NotBlank(message = "Имя категории не может быть пустым")
    @Size(max = 50, message = "Имя категории должно быть до 50 символов")
    String name;
}
