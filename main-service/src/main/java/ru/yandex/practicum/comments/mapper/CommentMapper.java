package ru.yandex.practicum.comments.mapper;

import jakarta.annotation.Nonnull;
import ru.yandex.practicum.comments.dto.CommentFullDto;
import ru.yandex.practicum.comments.dto.CommentShortDto;
import ru.yandex.practicum.comments.model.CommentModel;
import ru.yandex.practicum.event.mapper.EventMapper;
import ru.yandex.practicum.user.UserMapper;

public class CommentMapper {

    public static CommentShortDto toShortDto(@Nonnull CommentModel comment) {
        return CommentShortDto.builder()
                .id(comment.getId())
                .author(UserMapper.toShortDto(comment.getAuthor()))
                .text(comment.getText())
                .build();
    }

    public static CommentFullDto toFullDto(@Nonnull CommentModel comment) {
        return CommentFullDto.builder()
                .id(comment.getId())
                .event(EventMapper.toShortDto(comment.getEvent()))
                .author(UserMapper.toShortDto(comment.getAuthor()))
                .text(comment.getText())
                .created(comment.getCreated())
                .status(comment.getStatus())
                .build();
    }
}
