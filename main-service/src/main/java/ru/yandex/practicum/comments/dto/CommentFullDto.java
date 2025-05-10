package ru.yandex.practicum.comments.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.comments.model.CommentStatus;
import ru.yandex.practicum.event.dto.EventShortDto;
import ru.yandex.practicum.user.UserShortDto;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentFullDto {

    private Long id;

    private EventShortDto event;

    private UserShortDto author;

    private String text;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    private CommentStatus status;
}
