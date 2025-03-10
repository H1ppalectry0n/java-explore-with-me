package ru.yandex.practicum.comments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.user.UserShortDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentShortDto {

    private Long id;

    private UserShortDto author;

    @NotBlank
    @Size(min = 1, max = 500)
    private String text;
}
