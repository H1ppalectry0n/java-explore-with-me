package ru.yandex.practicum.compilation.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.EwmStatsClient;
import ru.yandex.practicum.compilation.dto.CompilationDto;
import ru.yandex.practicum.compilation.service.CompilationPublicService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CompilationPublicController {

    private final CompilationPublicService compilationPublicService;
    private final EwmStatsClient ewmStatsClient;

    @GetMapping
    public List<CompilationDto> findCompilations(@RequestParam(name = "pinned", required = false) Boolean pinned,
                                                 @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                                 @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                                 HttpServletRequest request) {
        ewmStatsClient.hit(request);
        log.info("CompilationPublic: find compilation pinned={}", pinned);
        return compilationPublicService.findCompilations(from, size, pinned);
    }

    @GetMapping("/{compId}")
    public CompilationDto findById(@PathVariable(name = "compId") long compId, HttpServletRequest request) {
        ewmStatsClient.hit(request);
        log.info("CompilationPublic: find compilation id={}", compId);
        return compilationPublicService.findById(compId);

    }
}
