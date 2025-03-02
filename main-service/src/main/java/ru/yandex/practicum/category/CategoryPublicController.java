package ru.yandex.practicum.category;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.EwmStatsClient;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CategoryPublicController {

    private final CategoryPublicService categoryPublicService;
    private final EwmStatsClient ewmStatsClient;

    @GetMapping
    public List<CategoryDto> findAll(@RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                     @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                     HttpServletRequest request) {
        ewmStatsClient.hit(request);
        log.info("CategoryPublic: findAll from={}, size={}", from, size);
        return categoryPublicService.findAll(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto findById(@PathVariable(name = "catId") long catId, HttpServletRequest request) {
        ewmStatsClient.hit(request);
        log.info("CategoryPublic: find by id={}", catId);
        return categoryPublicService.findById(catId);
    }
}
