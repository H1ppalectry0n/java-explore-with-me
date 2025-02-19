package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "endpoint_hits")
@Getter
@Setter
public class ViewStatsModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "app")
    private String app;

    @Column(nullable = false, name = "uri")
    private String uri;

    @Column(nullable = false, name = "ip") // Поддержка IPv4 и IPv6
    private String ip;

    @Column(nullable = false, name = "timestamp")
    private LocalDateTime timestamp = LocalDateTime.now();

}
