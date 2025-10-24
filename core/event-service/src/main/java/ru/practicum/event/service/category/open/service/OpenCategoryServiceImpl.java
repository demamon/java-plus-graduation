package ru.practicum.event.service.category.open.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.event.service.category.model.Category;
import ru.practicum.event.service.category.mapper.CategoryMapper;
import ru.practicum.event.service.category.repository.CategoryRepository;
import ru.practicum.interaction.api.dto.category.CategoryDto;
import ru.practicum.event.service.exception.NotFoundException;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenCategoryServiceImpl implements OpenCategoryService {
    private final CategoryRepository repository;

    @Override
    public Collection<CategoryDto> getCategories(int from, int size) {
        Pageable page = PageRequest.of(from, size);
        List<Category> categories = repository.findAll(page).getContent();
        if (categories.isEmpty()) {
            return List.of();
        }
        return categories.stream()
                .map(CategoryMapper::mapToCategoryDto)
                .sorted(Comparator.comparing(CategoryDto::getId))
                .toList();
    }

    @Override
    public CategoryDto getCategory(long catId) {
        return CategoryMapper.mapToCategoryDto(repository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория " + catId + " не найдена")));
    }

}
