package ru.yandex.practicum.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class UserShortDto {

    @NotNull
    private Long id;

    @NotBlank
    @Size(max = 255, message = "Длина имени не должна превышать 255 символов")
    private String name;
}
