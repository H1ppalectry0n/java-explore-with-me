package ru.yandex.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.yandex.practicum.event.model.EventModel;
import ru.yandex.practicum.user.UserModel;

import java.util.Optional;

public interface EventRepository extends JpaRepository<EventModel, Long>, JpaSpecificationExecutor<EventModel> {

    @EntityGraph(attributePaths = {"category", "initiator"})
    Page<EventModel> findAll(Specification<EventModel> specification, Pageable pageable);

    @EntityGraph(attributePaths = {"category", "initiator"})
    Page<EventModel> findAllByInitiator(UserModel initiator, Pageable pageable);

    @EntityGraph(attributePaths = {"category", "initiator"})
    Optional<EventModel> findByIdAndInitiator(Long id, UserModel initiator);

//    @Modifying
//    @Query("UPDATE EventModel e SET e.views = e.views + 1 WHERE e.id = :eventId")
//    void incrementViews(@Param("eventId") Long eventId);
//
//    @Modifying
//    @Query("UPDATE EventModel e SET e.views = e.views + 1 WHERE e.id IN :eventIds")
//    void incrementViews(@Param("eventIds") List<Long> eventIds);
}
