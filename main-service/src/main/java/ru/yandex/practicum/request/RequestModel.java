package ru.yandex.practicum.request;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.event.model.EventModel;
import ru.yandex.practicum.user.UserModel;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class RequestModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    @ToString.Exclude
    private EventModel event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_id", nullable = false)
    @ToString.Exclude
    private UserModel requester;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus requestStatus;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;
}
