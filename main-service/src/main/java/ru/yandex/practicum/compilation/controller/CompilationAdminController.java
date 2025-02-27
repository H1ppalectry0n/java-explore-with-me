package ru.yandex.practicum.compilation.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.EwmStatsClient;
import ru.yandex.practicum.compilation.dto.CompilationDto;
import ru.yandex.practicum.compilation.dto.OnCreateCompilationValidation;
import ru.yandex.practicum.compilation.dto.OnUpdateCompilationValidation;
import ru.yandex.practicum.compilation.dto.UpdateCompilationDto;
import ru.yandex.practicum.compilation.service.CompilationAdminService;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CompilationAdminController {

    private final CompilationAdminService compilationAdminService;
    private final EwmStatsClient ewmStatsClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createNewCompilation(@RequestBody @Validated(OnCreateCompilationValidation.class) UpdateCompilationDto updateCompilationDto,
                                               HttpServletRequest request) {
        ewmStatsClient.hit(request);
        log.debug("CompilationAdmin: create new compilation {}", updateCompilationDto.toString());
        return compilationAdminService.createNewCompilation(updateCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable(name = "compId") long compId, HttpServletRequest request) {
        ewmStatsClient.hit(request);
        log.debug("CompilationAdmin: delete compilation id={}", compId);
        compilationAdminService.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable(name = "compId") long compId,
                                            @RequestBody @Validated(OnUpdateCompilationValidation.class) UpdateCompilationDto updateCompilationDto,
                                            HttpServletRequest request) {
        ewmStatsClient.hit(request);
        log.debug("CompilationAdmin: update compilation id={} {}", compId, updateCompilationDto.toString());
        return compilationAdminService.updateCompilation(compId, updateCompilationDto);
    }
}
