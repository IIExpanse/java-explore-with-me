package ru.yandex.practicum.ewm.category.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.ewm.category.dto.CategoryDto;
import ru.yandex.practicum.ewm.category.dto.NewCategoryDto;
import ru.yandex.practicum.ewm.category.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "events", ignore = true)
    Category mapToModel(CategoryDto categoryDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    Category mapToNewModel(NewCategoryDto newCategoryDto);

    CategoryDto mapToDto(Category category);
}
