package ru.yandex.practicum.comments.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.EwmStatsClient;
import ru.yandex.practicum.comments.dto.CommentFullDto;
import ru.yandex.practicum.comments.dto.CommentShortDto;
import ru.yandex.practicum.comments.service.CommentPrivateService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CommentPrivateController {

    private final CommentPrivateService commentPrivateService;
    private final EwmStatsClient ewmStatsClient;

    @GetMapping("/comments")
    public List<CommentFullDto> findAllByAuthorId(@PathVariable(name = "userId") long authorId, HttpServletRequest request) {
        ewmStatsClient.hit(request);
        log.info("CommentPrivate: find all authorId={}", authorId);
        return commentPrivateService.findAllByAuthorId(authorId);
    }

    @PostMapping("/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentFullDto postComment(@PathVariable(name = "userId") long authorId,
                                      @PathVariable(name = "eventId") long eventId,
                                      @RequestBody @Valid CommentShortDto commentShortDto,
                                      HttpServletRequest request) {
        ewmStatsClient.hit(request);
        log.info("CommentPrivate: post comment authorId={}, eventId={}, comment={}", authorId, eventId, commentShortDto);
        return commentPrivateService.postComment(authorId, eventId, commentShortDto);
    }

    @PatchMapping("/comments/{commentId}")
    public CommentFullDto updateComment(@PathVariable(name = "userId") long authorId,
                                        @PathVariable(name = "commentId") long commentId,
                                        @RequestBody @Valid CommentShortDto commentShortDto,
                                        HttpServletRequest request) {
        ewmStatsClient.hit(request);
        log.info("CommentPrivate: update comment authorId={}, commentId={}, comment={}", authorId, commentId, commentShortDto);
        return commentPrivateService.changeComment(authorId, commentId, commentShortDto);
    }

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable(name = "userId") long authorId,
                              @PathVariable(name = "commentId") long commentId,
                              HttpServletRequest request) {
        ewmStatsClient.hit(request);
        log.info("CommentPrivate: delete comment authorId={}, commentId={}", authorId, commentId);
        commentPrivateService.deleteComment(authorId, commentId);
    }
}
