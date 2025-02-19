package ru.yandex.practicum;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class EndpointStatsDto {

    private String app;

    private String uri;

    private Long hits;
}
