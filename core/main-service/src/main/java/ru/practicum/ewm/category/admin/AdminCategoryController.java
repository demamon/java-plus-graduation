package ru.practicum.ewm.category.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.admin.service.AdminCategoryService;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryRequest;

@RestController
@RequestMapping(path = "/admin/categories")
@Slf4j
@RequiredArgsConstructor
public class AdminCategoryController {
    private final AdminCategoryService adminCategoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@Valid @RequestBody NewCategoryRequest category) {
        log.trace("Получаем запрос на создание категории {}", category);
        return adminCategoryService.createCategory(category);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCategory(@PathVariable long catId) {
        log.trace("Получаем запрос на удаление категории с id {}", catId);
        adminCategoryService.removeCategory(catId);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto patchCategory(@PathVariable long catId, @Valid @RequestBody NewCategoryRequest category) {
        log.trace("Получаем запрос на изменение категории с id {}. Новое имя {}", catId, category);
        return adminCategoryService.pathCategory(catId, category);
    }
}
