package ru.practicum.ewm.category.open.service;

import ru.practicum.ewm.category.dto.CategoryDto;

import java.util.Collection;

public interface OpenCategoryService {
    Collection<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategory(long catId);
}
