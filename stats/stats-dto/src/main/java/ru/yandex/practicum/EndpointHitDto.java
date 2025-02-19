package ru.yandex.practicum;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class EndpointHitDto {

    @NotBlank(message = "Необходимо указать идентификатор сервиса")
    @Size(max = 255, message = "Длина идентификатора сервиса не должна превышать 255 символов")
    private String app;

    @NotBlank(message = "Необходимо указать URI")
    @Size(max = 255, message = "Длина URI не должна превышать 255 символов")
    private String uri;

    @NotBlank(message = "Необходимо указать IP-адрес пользователя")
    @Size(max = 45, message = "Длина ip не должна превышать 45 символов")
    private String ip;

    @NotNull(message = "Необходимо указать дату и время")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}
