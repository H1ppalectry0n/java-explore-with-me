package ru.yandex.practicum.comments.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.comments.dto.CommentFullDto;
import ru.yandex.practicum.comments.dto.CommentsStatusChangeDto;
import ru.yandex.practicum.comments.mapper.CommentMapper;
import ru.yandex.practicum.comments.model.CommentModel;
import ru.yandex.practicum.comments.model.CommentStatus;
import ru.yandex.practicum.comments.repository.CommentRepository;
import ru.yandex.practicum.comments.repository.CommentSpecification;
import ru.yandex.practicum.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentAdminService {

    private final CommentRepository commentRepository;

    public void deleteComment(long commentId) {
        commentRepository.deleteById(commentId);
    }

    public List<CommentFullDto> changeCommentsStatus(CommentsStatusChangeDto commentsStatusChangeDto) {
        List<CommentModel> comments = commentRepository.findAllById(commentsStatusChangeDto.getIds());

        if (comments.size() != commentsStatusChangeDto.getIds().size()) {
            throw new NotFoundException("Comments not found");
        }

        for (CommentModel comment : comments) {
            comment.setStatus(commentsStatusChangeDto.getStatus());
        }

        return commentRepository.saveAll(comments).stream().map(CommentMapper::toFullDto).toList();
    }

    public List<CommentFullDto> findAllFiltered(int from, int size, Long authorId, Long eventId, String text, CommentStatus status) {
        Specification<CommentModel> spec = CommentSpecification.withFiltersForAdmin(authorId, eventId, status);
        List<CommentModel> comments = commentRepository.findAll(spec, PageRequest.of(from, size));

        if (text != null && !text.isBlank()) {
            comments = comments.stream().filter(c -> c.getText().contains(text)).toList();
        }

        return comments.stream().map(CommentMapper::toFullDto).toList();
    }
}
