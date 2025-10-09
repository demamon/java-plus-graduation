package ru.practicum.ewm.category.admin.service;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryRequest;

public interface AdminCategoryService {
    CategoryDto createCategory(NewCategoryRequest category);

    void removeCategory(long catId);

    CategoryDto pathCategory(long catId, NewCategoryRequest category);
}
