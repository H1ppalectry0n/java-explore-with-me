package ru.yandex.practicum.comments.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.comments.model.CommentModel;
import ru.yandex.practicum.comments.model.CommentStatus;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentModel, Long> {

    @EntityGraph(attributePaths = "author")
    List<CommentModel> findAllByEventIdAndStatus(long eventId, CommentStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "event"})
    List<CommentModel> findAllByAuthorId(long authorId);

    @EntityGraph(attributePaths = {"author", "event"})
    List<CommentModel> findAll(Specification<CommentModel> specification, Pageable pageable);

}
