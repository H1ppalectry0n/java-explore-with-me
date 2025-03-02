package ru.yandex.practicum.category;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.ConstraintException;
import ru.yandex.practicum.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class CategoryAdminService {

    private final CategoryRepository categoryRepository;

    public CategoryDto createNewCategory(@Nonnull CategoryDto categoryDto) {
        try {
            return CategoryMapper.toDto(categoryRepository.save(CategoryMapper.toModel(categoryDto)));
        } catch (DataIntegrityViolationException e) {
            throw new ConstraintException(e.getMessage());
        }
    }

    public void deleteCategory(long categoryId) {
        try {
            categoryRepository.deleteById(categoryId);
        } catch (DataIntegrityViolationException e) {
            throw new ConstraintException(e.getMessage());
        }
    }

    public CategoryDto updateCategoryById(long catId, @Nonnull CategoryDto categoryDto) {
        CategoryModel category = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Category with id=%d was not found".formatted(catId))
        );

        category.setName(categoryDto.getName());

        try {
            return CategoryMapper.toDto(categoryRepository.save(category));
        } catch (DataIntegrityViolationException e) {
            throw new ConstraintException(e.getMessage());
        }
    }
}
