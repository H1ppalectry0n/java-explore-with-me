package ru.yandex.practicum.comments.repository;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ru.yandex.practicum.comments.model.CommentModel;
import ru.yandex.practicum.comments.model.CommentStatus;

import java.util.ArrayList;
import java.util.List;

public class CommentSpecification {

    public static Specification<CommentModel> withFiltersForAdmin(Long authorId, Long eventId, CommentStatus status) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (authorId != null) {
                predicates.add(criteriaBuilder.equal(root.get("author").get("id"), authorId));
            }

            if (eventId != null) {
                predicates.add(criteriaBuilder.equal(root.get("event").get("id"), eventId));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
