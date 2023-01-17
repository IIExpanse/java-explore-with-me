package ru.yandex.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewm.category.dto.CategoryDto;
import ru.yandex.practicum.ewm.category.dto.NewCategoryDto;
import ru.yandex.practicum.ewm.category.service.CategoryService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@ResponseStatus(HttpStatus.OK)
public class CategoryController {

    private final CategoryService service;

    @PostMapping(path = "/admin/categories")
    public CategoryDto addCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        return service.addCategory(newCategoryDto);
    }

    @GetMapping(path = "/categories/{catId}")
    public CategoryDto getCategory(@PathVariable Long catId) {
        return service.getCategory(catId);
    }

    @GetMapping(path = "/categories")
    public Collection<CategoryDto> getCategories(
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return service.getCategories(from, size);
    }

    @PatchMapping(path = "/admin/categories")
    public CategoryDto updateCategory(@RequestBody @Valid CategoryDto categoryDto) {
        return service.updateCategory(categoryDto);
    }

    @DeleteMapping(path = "/admin/categories/{catId}")
    public void deleteCategory(@PathVariable Long catId) {
        service.deleteCategory(catId);
    }
}
