package ru.yandex.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.EwmStatsClient;
import ru.yandex.practicum.event.dto.EventFullDto;
import ru.yandex.practicum.event.dto.NewEventDto;
import ru.yandex.practicum.event.dto.UpdateEventUserDto;
import ru.yandex.practicum.event.service.EventPrivateService;
import ru.yandex.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.yandex.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventPrivateController {

    private final EventPrivateService eventPrivateService;
    private final EwmStatsClient ewmStatsClient;

    @GetMapping
    public List<EventFullDto> findByUserId(@PathVariable(name = "userId") long userId,
                                           @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                           @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                           HttpServletRequest request) {
        ewmStatsClient.hit(request);
        log.info("EventPrivate: find events by userId={}", userId);
        return eventPrivateService.findByUserId(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto postNewEvent(@PathVariable(name = "userId") long userId,
                                     @RequestBody @Valid NewEventDto event,
                                     HttpServletRequest request) {
        ewmStatsClient.hit(request);
        log.info("EventPrivate: post new event by userId={} {}", userId, event);
        return eventPrivateService.postNewEvent(userId, event);
    }

    @GetMapping("/{eventId}")
    public EventFullDto findByEventIdForUser(@PathVariable(name = "userId") long userId,
                                             @PathVariable(name = "eventId") long eventId,
                                             HttpServletRequest request) {
        ewmStatsClient.hit(request);
        log.info("EventPrivate: find event id={} by userId={}", eventId, userId);
        return eventPrivateService.findByEventIdForUser(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable(name = "userId") long userId,
                                    @PathVariable(name = "eventId") long eventId,
                                    @RequestBody @Valid UpdateEventUserDto updatedEvent,
                                    HttpServletRequest request) {
        ewmStatsClient.hit(request);
        log.info("EventPrivate: update event id={} by userId={} event={}", eventId, userId, updatedEvent);
        return eventPrivateService.updateEvent(userId, eventId, updatedEvent);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> findAllRequestsForEvent(@PathVariable(name = "userId") long userId,
                                                                 @PathVariable(name = "eventId") long eventId,
                                                                 HttpServletRequest request) {
        ewmStatsClient.hit(request);
        log.info("EventPrivate: find request for event id={} by userId={}", eventId, userId);
        return eventPrivateService.findAllRequestsForEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequests(@PathVariable(name = "userId") long userId,
                                                         @PathVariable(name = "eventId") long eventId,
                                                         @RequestBody @Valid EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest,
                                                         HttpServletRequest request) {
        ewmStatsClient.hit(request);
        log.info("EventPrivate: update request for event id={} by userId={} {}", eventId, userId, eventRequestStatusUpdateRequest);
        return eventPrivateService.updateRequests(userId, eventId, eventRequestStatusUpdateRequest);

    }
}
