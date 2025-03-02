package ru.yandex.practicum.category;

import jakarta.annotation.Nonnull;

public class CategoryMapper {

    public static CategoryModel toModel(@Nonnull CategoryDto categoryDto) {
        CategoryModel category = new CategoryModel();
        category.setId(categoryDto.getId());
        category.setName(categoryDto.getName());

        return category;
    }

    public static CategoryDto toDto(@Nonnull CategoryModel categoryModel) {
        return CategoryDto.builder()
                .id(categoryModel.getId())
                .name(categoryModel.getName())
                .build();
    }
}
