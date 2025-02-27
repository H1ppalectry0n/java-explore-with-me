package ru.yandex.practicum.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CategoryDto {

    private Long id;

    @NotBlank
    @Size(min = 1, max = 50, message = "Поле name должно быть от 1 до 50 символов")
    private String name;
}
