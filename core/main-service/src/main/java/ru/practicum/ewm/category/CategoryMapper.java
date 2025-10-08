package ru.practicum.ewm.category;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryRequest;

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
