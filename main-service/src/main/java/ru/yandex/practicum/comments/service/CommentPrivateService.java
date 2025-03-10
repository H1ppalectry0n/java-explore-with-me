package ru.yandex.practicum.comments.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.comments.dto.CommentFullDto;
import ru.yandex.practicum.comments.dto.CommentShortDto;
import ru.yandex.practicum.comments.mapper.CommentMapper;
import ru.yandex.practicum.comments.model.CommentModel;
import ru.yandex.practicum.comments.model.CommentStatus;
import ru.yandex.practicum.comments.repository.CommentRepository;
import ru.yandex.practicum.event.model.EventModel;
import ru.yandex.practicum.event.model.EventState;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.exception.ConstraintException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.user.UserModel;
import ru.yandex.practicum.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentPrivateService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public List<CommentFullDto> findAllByAuthorId(long authorId) {
        userRepository.findById(authorId).orElseThrow(() -> new NotFoundException(
                "User with id=%d was not found".formatted(authorId)
        ));

        return commentRepository.findAllByAuthorId(authorId).stream()
                .map(CommentMapper::toFullDto)
                .toList();
    }

    public CommentFullDto postComment(long authorId, long eventId, CommentShortDto commentShortDto) {
        UserModel author = userRepository.findById(authorId).orElseThrow(() -> new NotFoundException(
                "User with id=%d was not found".formatted(authorId)
        ));

        EventModel event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                "Event with id=%d was not found".formatted(eventId)
        ));

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConstraintException("Event mast be PUBLISHED");
        }

        CommentModel comment = CommentModel.builder()
                .text(commentShortDto.getText())
                .author(author)
                .event(event)
                .status(CommentStatus.PENDING)
                .build();

        return CommentMapper.toFullDto(commentRepository.save(comment));
    }

    public CommentFullDto changeComment(long authorId, long commentId, CommentShortDto commentShortDto) {
        userRepository.findById(authorId).orElseThrow(() -> new NotFoundException(
                "User with id=%d was not found".formatted(authorId)
        ));

        CommentModel comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(
                "Comment with id=%d was not found".formatted(commentId)
        ));

        if (comment.getStatus() != CommentStatus.PENDING) {
            throw new ConstraintException("Comment already must be on moderation");
        }

        if (comment.getAuthor().getId() != authorId) {
            throw new ConstraintException("Only author can change comment");
        }

        comment.setText(commentShortDto.getText());

        return CommentMapper.toFullDto(commentRepository.save(comment));
    }

    public void deleteComment(long authorId, long commentId) {
        userRepository.findById(authorId).orElseThrow(() -> new NotFoundException(
                "User with id=%d was not found".formatted(authorId)
        ));

        CommentModel comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(
                "Comment with id=%d was not found".formatted(commentId)
        ));

        if (comment.getAuthor().getId() != authorId) {
            throw new ConstraintException("Only author can change comment");
        }

        commentRepository.delete(comment);
    }
}
