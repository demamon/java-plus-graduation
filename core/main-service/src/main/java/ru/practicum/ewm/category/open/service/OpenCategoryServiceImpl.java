package ru.practicum.ewm.category.open.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.exception.NotFoundException;

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
