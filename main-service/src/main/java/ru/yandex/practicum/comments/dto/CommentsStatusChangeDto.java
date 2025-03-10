package ru.yandex.practicum.comments.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.comments.model.CommentStatus;

import java.util.Set;

@Data
public class CommentsStatusChangeDto {

    @NotNull
    private Set<Long> ids;

    @NotNull
    private CommentStatus status;
}
