package ru.yandex.practicum.request.model;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.event.model.EventModel;
import ru.yandex.practicum.request.RequestModel;
import ru.yandex.practicum.request.RequestStatus;
import ru.yandex.practicum.user.UserModel;

import java.util.List;

public interface RequestRepository extends JpaRepository<RequestModel, Long> {

    @EntityGraph(attributePaths = {"event", "requester"})
    List<RequestModel> findAllByRequester(UserModel requester);

    long countByRequesterId(long requesterId);

    long countByEventIdAndRequestStatus(long eventId, RequestStatus requestStatus);

    @Query("""
        SELECT r.event.id AS eventId, COUNT(r) AS participantCount
        FROM RequestModel r
        WHERE r.event.id IN :eventIds AND r.requestStatus = 'CONFIRMED'
        GROUP BY r.event.id
    """)
    List<ParticipantCount> findParticipantCount(List<Long> eventIds);

    @EntityGraph(attributePaths = {"event", "requester"})
    List<RequestModel> findAllByEvent(EventModel event);
}
