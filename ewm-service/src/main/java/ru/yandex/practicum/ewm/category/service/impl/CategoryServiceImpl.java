package ru.yandex.practicum.ewm.category.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.category.dto.CategoryDto;
import ru.yandex.practicum.ewm.category.dto.NewCategoryDto;
import ru.yandex.practicum.ewm.category.exception.CantDeleteUsedCategoryException;
import ru.yandex.practicum.ewm.category.exception.CategoryNameAlreadyExistsException;
import ru.yandex.practicum.ewm.category.exception.CategoryNotFoundException;
import ru.yandex.practicum.ewm.category.mapper.CategoryMapper;
import ru.yandex.practicum.ewm.category.model.Category;
import ru.yandex.practicum.ewm.category.repository.CategoryRepository;
import ru.yandex.practicum.ewm.category.service.CategoryService;
import ru.yandex.practicum.ewm.event.repository.EventRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper mapper;

    @Override
    @Transactional
    public CategoryDto addCategory(NewCategoryDto newCategory) {
        if (categoryRepository.existsCategoryByName(newCategory.getName())) {
            throw new CategoryNameAlreadyExistsException(
                    String.format("Ошибка при создании категории: имя '%s' уже занято.", newCategory.getName()));
        }
        Category category = categoryRepository.save(mapper.mapToNewModel(newCategory));

        log.debug("Добавлена новая категория: {}.", category);
        return mapper.mapToDto(category);
    }

    @Override
    public CategoryDto getCategory(long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(
                String.format("Ошибка при получении категории: категория с id=%d не найдена.", id)));

        log.trace("Запрошена категория с id={}.", id);
        return mapper.mapToDto(category);
    }

    @Override
    public Collection<CategoryDto> getCategories(int from, int size) {
        log.trace("Запрошен список категорий.");
        return categoryRepository.findAll(Pageable.ofSize(size)).stream()
                .skip(from)
                .map(mapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        if (!categoryRepository.existsById(categoryDto.getId())) {
            throw new CategoryNotFoundException(
                    String.format(
                            "Ошибка при обновлении категории: категория с id=%d не найдена.",
                            categoryDto.getId()));

        } else if (categoryRepository.existsCategoryByNameAndIdNot(categoryDto.getName(), categoryDto.getId())) {
            throw new CategoryNameAlreadyExistsException(
                    String.format("Ошибка при обновлении категории: имя '%s' уже занято.", categoryDto.getName()));
        }
        Category category = categoryRepository.save(mapper.mapToModel(categoryDto));

        log.debug("Изменена категория: {}", category);
        return mapper.mapToDto(category);
    }

    @Override
    @Transactional
    public void deleteCategory(long id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException(
                    String.format("Ошибка при удалении категории: категория с id=%d не найдена.", id));

        } else if (eventRepository.existsByCategoryId(id)) {
            throw new CantDeleteUsedCategoryException(String.format("Ошибка при удалении категории с id=%d: " +
                    "с категорией связаны события.", id));
        }

        categoryRepository.deleteById(id);
        log.debug("Удалена категория с id={}.", id);
    }
}
