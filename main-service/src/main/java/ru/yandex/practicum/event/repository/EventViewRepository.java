package ru.yandex.practicum.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.event.model.EventViewModel;

public interface EventViewRepository extends JpaRepository<EventViewModel, Long> {

    @Query("SELECT count(distinct ev.userIp) FROM EventViewModel ev WHERE ev.event.id = :eventId")
    long countUserIpDistinctByEventId(@Param(value = "eventId") long eventId);
}
