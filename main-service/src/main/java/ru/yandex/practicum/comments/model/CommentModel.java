package ru.yandex.practicum.comments.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.event.model.EventModel;
import ru.yandex.practicum.user.UserModel;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CommentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    @ToString.Exclude
    private EventModel event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @ToString.Exclude
    private UserModel author;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "created")
    private LocalDateTime created = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private CommentStatus status;
}
