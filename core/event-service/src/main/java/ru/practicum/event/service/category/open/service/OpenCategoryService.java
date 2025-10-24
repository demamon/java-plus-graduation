package ru.practicum.event.service.category.open.service;

import ru.practicum.interaction.api.dto.category.CategoryDto;

import java.util.Collection;

public interface OpenCategoryService {
    Collection<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategory(long catId);
}
