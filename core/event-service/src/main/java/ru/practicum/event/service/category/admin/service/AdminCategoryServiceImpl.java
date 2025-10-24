package ru.practicum.event.service.category.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.service.category.model.Category;
import ru.practicum.event.service.category.mapper.CategoryMapper;
import ru.practicum.event.service.category.repository.CategoryRepository;
import ru.practicum.interaction.api.dto.category.CategoryDto;
import ru.practicum.interaction.api.dto.category.NewCategoryRequest;
import ru.practicum.event.service.event.repository.EventRepository;
import ru.practicum.event.service.exception.ConflictException;
import ru.practicum.event.service.exception.NotFoundException;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdminCategoryServiceImpl implements AdminCategoryService {
    private final CategoryRepository repository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CategoryDto createCategory(NewCategoryRequest category) {
        isDuplicateName(category.getName());
        Category newCategory = repository.save(CategoryMapper.mapFromRequest(category));
        log.debug("категория после добавления в бд {}", newCategory);
        return CategoryMapper.mapToCategoryDto(newCategory);
    }

    @Transactional
    @Override
    public void removeCategory(long catId) {
        getCategoryBiId(catId);
        if (eventRepository.existsByCategoryId(catId)) {
            throw new ConflictException("Нельзя удалить категорию: существуют связанные события");
        }
        log.debug("удаляем категорию с id {}", catId);
        repository.deleteById(catId);
    }

    @Override
    public CategoryDto pathCategory(long catId, NewCategoryRequest category) {
        Category oldCategory = getCategoryBiId(catId);
        if (oldCategory.getTitle().equals(category.getName())) {
            return CategoryMapper.mapToCategoryDto(oldCategory);
        }
        isDuplicateName(category.getName());
        oldCategory.setTitle(category.getName());
        repository.save(oldCategory);
        return CategoryMapper.mapToCategoryDto(oldCategory);
    }

    private Category getCategoryBiId(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория " + id + " не найдена"));
    }

    private void isDuplicateName(String name) {
        if (repository.findByTitleLike(name).isPresent()) {
            log.debug("Название категории {} уже есть в базе", name);
            throw new ConflictException("Название категории " + name + "уже есть в базе");
        }
    }
}
