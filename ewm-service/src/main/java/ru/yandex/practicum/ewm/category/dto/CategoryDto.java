package ru.yandex.practicum.ewm.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

    @NotNull(message = "Идентификатор категории не может быть пустым.")
    private Long id;
    @NotBlank(message = "Название категории не может быть пустым.")
    private String name;
}
