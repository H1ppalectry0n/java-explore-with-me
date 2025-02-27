package ru.yandex.practicum.request;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.EwmStatsClient;
import ru.yandex.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RequestPrivateController {

    public final RequestPrivateService requestPrivateService;
    private final EwmStatsClient ewmStatsClient;

    @GetMapping
    public List<ParticipationRequestDto> findAllByUserId(@PathVariable(name = "userId") long userId, HttpServletRequest request) {
        ewmStatsClient.hit(request);
        log.debug("RequestPrivate: find all request for user with id={}", userId);
        return requestPrivateService.findAllByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createNewRequest(@PathVariable(name = "userId") long userId, @RequestParam(name = "eventId") long eventId, HttpServletRequest request) {
        ewmStatsClient.hit(request);
        log.debug("RequestPrivate: create new request userId={}, eventId={}", userId, eventId);
        return requestPrivateService.createNewRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable(name = "userId") long userId, @PathVariable(name = "requestId") long requestId, HttpServletRequest request) {
        ewmStatsClient.hit(request);
        log.debug("RequestPrivate: cancel request userId={}, requestId={}", userId, requestId);
        return requestPrivateService.cancelRequest(userId, requestId);
    }
}
