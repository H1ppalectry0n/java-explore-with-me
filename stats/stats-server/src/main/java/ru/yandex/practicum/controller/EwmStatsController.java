package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.EndpointHitDto;
import ru.yandex.practicum.EndpointStatsDto;
import ru.yandex.practicum.service.EwmStatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class EwmStatsController {

    private final EwmStatsService ewmStatsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void hit(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        log.debug("hit: {}", endpointHitDto.toString());
        ewmStatsService.hit(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<EndpointStatsDto> stats(
            @RequestParam(name = "start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam(name = "end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(required = false, name = "uris") List<String> uris,
            @RequestParam(required = false, name = "unique", defaultValue = "false") boolean unique) {
        return ewmStatsService.stats(start, end, uris, unique);
    }
}
