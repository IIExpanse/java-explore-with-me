package ru.yandex.practicum.ewm.category.service;

import ru.yandex.practicum.ewm.category.dto.CategoryDto;
import ru.yandex.practicum.ewm.category.dto.NewCategoryDto;

import java.util.Collection;

public interface CategoryService {

    CategoryDto addCategory(NewCategoryDto newCategory);

    CategoryDto getCategory(long id);

    Collection<CategoryDto> getCategories(int from, int size);

    CategoryDto updateCategory(CategoryDto categoryDto);

    void deleteCategory(long id);
}
