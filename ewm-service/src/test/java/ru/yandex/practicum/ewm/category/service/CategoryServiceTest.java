package ru.yandex.practicum.ewm.category.service;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.ewm.category.dto.CategoryDto;
import ru.yandex.practicum.ewm.category.dto.NewCategoryDto;
import ru.yandex.practicum.ewm.category.exception.CategoryNameAlreadyExistsException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AllArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = "classpath:schema-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CategoryServiceTest {

    private CategoryService service;

    @Test
    public void addCategoryTest() {
        CategoryDto categoryDto = service.addCategory(makeDefaultRequest());

        assertEquals(categoryDto, service.getCategory(categoryDto.getId()));
    }

    @Test
    public void shouldThrowExceptionForDuplicateName() {
        service.addCategory(makeDefaultRequest());

        assertThrows(CategoryNameAlreadyExistsException.class, () -> service.addCategory(makeDefaultRequest()));
    }

    @Test
    public void getCategoriesTest() {
        CategoryDto categoryDto1 = service.addCategory(makeDefaultRequest());
        NewCategoryDto request2 = makeDefaultRequest();
        request2.setName("Спектакли");
        CategoryDto categoryDto2 = service.addCategory(request2);

        assertEquals(List.of(categoryDto1, categoryDto2), service.getCategories(0, 10));
    }

    @Test
    public void updateCategoryTest() {
        CategoryDto categoryDto = service.addCategory(makeDefaultRequest());
        categoryDto.setName("Спектакли");
        CategoryDto updatedCategory = service.updateCategory(categoryDto);

        assertEquals(categoryDto, service.getCategory(updatedCategory.getId()));
    }

    @Test
    public void deleteCategoryTest() {
        CategoryDto categoryDto = service.addCategory(makeDefaultRequest());
        service.deleteCategory(categoryDto.getId());

        assertTrue(service.getCategories(0, 10).isEmpty());
    }

    private NewCategoryDto makeDefaultRequest() {
        return NewCategoryDto.builder().name("Концерты").build();
    }
}
