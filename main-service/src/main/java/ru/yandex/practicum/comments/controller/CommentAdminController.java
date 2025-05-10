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
import ru.yandex.practicum.comments.dto.CommentsStatusChangeDto;
import ru.yandex.practicum.comments.model.CommentStatus;
import ru.yandex.practicum.comments.service.CommentAdminService;

import java.util.List;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CommentAdminController {

    private final CommentAdminService commentAdminService;
    private final EwmStatsClient ewmStatsClient;

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable(name = "commentId") long commentId, HttpServletRequest request) {
        ewmStatsClient.hit(request);
        log.info("CommentAdmin: delete comment commentId={}", commentId);
        commentAdminService.deleteComment(commentId);
    }

    @PatchMapping
    public List<CommentFullDto> updateCommentsStatus(@RequestBody @Valid CommentsStatusChangeDto commentsStatusChangeDto,
                                                     HttpServletRequest request) {
        ewmStatsClient.hit(request);
        log.info("CommentAdmin: patch comments {}", commentsStatusChangeDto);
        return commentAdminService.changeCommentsStatus(commentsStatusChangeDto);
    }

    @GetMapping
    public List<CommentFullDto> findAllFiltered(@RequestParam(name = "authorId", required = false) Long authorId,
                                                @RequestParam(name = "eventId", required = false) Long eventId,
                                                @RequestParam(name = "text", required = false) String text,
                                                @RequestParam(name = "status", required = false) CommentStatus status,
                                                @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                                @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                                HttpServletRequest request) {
        ewmStatsClient.hit(request);
        log.info("CommentAdmin: find comments");
        return commentAdminService.findAllFiltered(from, size, authorId, eventId, text, status);
    }
}
