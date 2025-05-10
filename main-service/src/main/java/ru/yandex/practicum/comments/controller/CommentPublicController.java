package ru.yandex.practicum.comments.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.EwmStatsClient;
import ru.yandex.practicum.comments.dto.CommentShortDto;
import ru.yandex.practicum.comments.service.CommentPublicService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentPublicController {

    private final CommentPublicService commentPublicService;
    private final EwmStatsClient ewmStatsClient;


    @GetMapping("/events/{eventId}/comments")
    public List<CommentShortDto> findAll(@PathVariable(name = "eventId") long eventId,
                                         @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                         @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                         HttpServletRequest request) {
        ewmStatsClient.hit(request);
        log.info("CommentPublic: find all eventId={}", eventId);
        return commentPublicService.findAll(eventId, from, size);
    }
}
