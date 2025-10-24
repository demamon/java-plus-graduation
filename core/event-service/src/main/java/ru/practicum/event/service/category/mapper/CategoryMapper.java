package ru.practicum.event.service.category.mapper;

import ru.practicum.interaction.api.dto.category.CategoryDto;
import ru.practicum.interaction.api.dto.category.NewCategoryRequest;
import ru.practicum.event.service.category.model.Category;

public class CategoryMapper {
    public static CategoryDto mapToCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getTitle())
                .build();
    }

    public static Category mapFromRequest(NewCategoryRequest categoryRequest) {
        return new Category(
                categoryRequest.getName()
        );
    }
}
