package ru.yandex.practicum.category;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.EwmStatsClient;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
@Slf4j
@Validated
public class CategoryAdminController {

    private final CategoryAdminService categoryAdminService;
    private final EwmStatsClient ewmStatsClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createNewCategory(@RequestBody @Valid CategoryDto categoryDto, HttpServletRequest request) {
        ewmStatsClient.hit(request);
        log.debug("CategoryAdmin: create new category {}", categoryDto.toString());
        return categoryAdminService.createNewCategory(categoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable(name = "catId") long catId, HttpServletRequest request) {
        ewmStatsClient.hit(request);
        log.debug("CategoryAdmin: deleting category with id = {}", catId);
        categoryAdminService.deleteCategory(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategoryById(@PathVariable(name = "catId") long catId,
                                          @RequestBody @Valid CategoryDto categoryDto,
                                          HttpServletRequest request) {
        ewmStatsClient.hit(request);
        log.debug("CategoryAdmin: updating category with id = {} : {}", catId, categoryDto.toString());
        return categoryAdminService.updateCategoryById(catId, categoryDto);
    }
}
