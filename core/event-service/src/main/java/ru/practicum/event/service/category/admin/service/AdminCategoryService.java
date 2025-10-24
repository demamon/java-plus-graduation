package ru.practicum.event.service.category.admin.service;

import ru.practicum.interaction.api.dto.category.CategoryDto;
import ru.practicum.interaction.api.dto.category.NewCategoryRequest;

public interface AdminCategoryService {
    CategoryDto createCategory(NewCategoryRequest category);

    void removeCategory(long catId);

    CategoryDto pathCategory(long catId, NewCategoryRequest category);
}
