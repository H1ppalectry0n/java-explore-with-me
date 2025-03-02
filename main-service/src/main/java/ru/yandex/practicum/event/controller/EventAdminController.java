package ru.yandex.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.EwmStatsClient;
import ru.yandex.practicum.event.dto.EventFullDto;
import ru.yandex.practicum.event.dto.UpdateEventAdminDto;
import ru.yandex.practicum.event.model.EventState;
import ru.yandex.practicum.event.service.EventAdminService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class EventAdminController {

    private final EventAdminService eventAdminService;
    private final EwmStatsClient ewmStatsClient;

    @GetMapping
    public List<EventFullDto> findAllWithFilters(@RequestParam(name = "users", required = false) List<Long> userIds,
                                                 @RequestParam(name = "states", required = false) List<EventState> states,
                                                 @RequestParam(name = "categories", required = false) List<Long> categoryIds,
                                                 @RequestParam(name = "rangeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                 @RequestParam(name = "rangeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                 @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                                 @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                                 HttpServletRequest request) {
        ewmStatsClient.hit(request);
        log.info("EventAdmin: find events with param = { users = {}, states = {}, categories = {}, start = {}, end = {} }",
                userIds, states, categoryIds, rangeStart, rangeEnd);
        return eventAdminService.findAllWithFilters(from, size, userIds, states, categoryIds, rangeStart, rangeEnd);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patchEventWithId(@PathVariable(name = "eventId") long eventId, @RequestBody @Valid UpdateEventAdminDto updateEventDto, HttpServletRequest request) {
        ewmStatsClient.hit(request);
        log.info("EventAdmin: path event with id={} {}", eventId, updateEventDto);
        return eventAdminService.patchEventWithId(eventId, updateEventDto);
    }
}
