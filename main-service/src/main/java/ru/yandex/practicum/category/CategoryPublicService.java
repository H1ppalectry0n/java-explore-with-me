package ru.yandex.practicum.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryPublicService {

    private final CategoryRepository categoryRepository;

    public List<CategoryDto> findAll(int from, int size) {
        return categoryRepository.findAll(PageRequest.of(from, size)).stream().map(CategoryMapper::toDto).toList();
    }

    public CategoryDto findById(long catId) {
        return CategoryMapper.toDto(
                categoryRepository.findById(catId).orElseThrow(() ->
                        new NotFoundException("Category with id=%d was not found".formatted(catId)))
        );
    }
}
