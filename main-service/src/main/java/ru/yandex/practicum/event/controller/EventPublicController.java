package ru.yandex.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.EwmStatsClient;
import ru.yandex.practicum.event.dto.EventFullDto;
import ru.yandex.practicum.event.dto.EventSortType;
import ru.yandex.practicum.event.service.EventPublicService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@Transactional
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventPublicController {

    private final EventPublicService eventPublicService;
    private final EwmStatsClient ewmStatsClient;

    @GetMapping
    public List<EventFullDto> findAllWithFilters(@RequestParam(name = "text", required = false) String text,
                                                 @RequestParam(name = "categories", required = false) List<Long> categoryIds,
                                                 @RequestParam(name = "paid", required = false) Boolean paid,
                                                 @RequestParam(name = "rangeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                 @RequestParam(name = "rangeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                 @RequestParam(name = "onlyAvailable", required = false) Boolean onlyAvailable,
                                                 @RequestParam(name = "sort", required = false) EventSortType sortType,
                                                 @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                                 @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                                 HttpServletRequest request) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new IllegalArgumentException("rangeStart должен быть раньше rangeEnd");
        }

        ewmStatsClient.hit(request);
        log.debug("EventPublic: find events with param = { text = {}, categoryIds={}, paid={}, rangeStart={}, rangeEnd={}, onlyAvailable={} }",
                text, categoryIds, paid, rangeStart, rangeEnd, onlyAvailable);
        return eventPublicService.findAllWithFilters(from, size, request.getRemoteAddr(), text, categoryIds, paid, rangeStart, rangeEnd, onlyAvailable, sortType);
    }

    @GetMapping("/{eventId}")
    public EventFullDto findById(@PathVariable(name = "eventId") long eventId, HttpServletRequest request) {
        ewmStatsClient.hit(request);
        log.debug("EventPublic: find event by id={}", eventId);
        return eventPublicService.findById(eventId, request.getRemoteAddr());
    }
}
