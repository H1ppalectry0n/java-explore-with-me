package ru.yandex.practicum.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class UserDto {

    private Long id;

    @NotBlank
    @Size(min = 2, max = 250, message = "Длина имени должна быть от 2 до 250 символов")
    private String name;

    @Email
    @NotBlank
    @Size(min = 6, max = 254, message = "Длина email должна быть от 6 до 254 символов")
    private String email;
}
