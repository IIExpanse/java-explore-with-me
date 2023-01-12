package ru.yandex.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewm.category.dto.CategoryDto;
import ru.yandex.practicum.ewm.category.dto.NewCategoryDto;
import ru.yandex.practicum.ewm.category.service.CategoryService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService service;

    @PostMapping(path = "/admin/categories")
    public ResponseEntity<CategoryDto> addCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        return ResponseEntity.ok(service.addCategory(newCategoryDto));
    }

    @GetMapping(path = "/categories/{catId}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable Long catId) {
        return ResponseEntity.ok(service.getCategory(catId));
    }

    @GetMapping(path = "/categories")
    public ResponseEntity<Collection<CategoryDto>> getCategories(
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return ResponseEntity.ok(service.getCategories(from, size));
    }

    @PatchMapping(path = "/admin/categories")
    public ResponseEntity<CategoryDto> updateCategory(@RequestBody @Valid CategoryDto categoryDto) {
        return ResponseEntity.ok(service.updateCategory(categoryDto));
    }

    @DeleteMapping(path = "/admin/categories/{catId}")
    @ResponseStatus(code = HttpStatus.OK)
    public void deleteCategory(@PathVariable Long catId) {
        service.deleteCategory(catId);
    }
}
