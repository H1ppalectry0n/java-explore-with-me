package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.EndpointStatsDto;
import ru.yandex.practicum.model.ViewStatsModel;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EwmStatsRepository extends JpaRepository<ViewStatsModel, Long> {

    @Query("""
                SELECT new ru.yandex.practicum.EndpointStatsDto(
                    e.app, e.uri,
                    CASE
                        WHEN :unique = TRUE THEN COUNT(DISTINCT e.ip)
                        ELSE COUNT(e.ip)
                    END
                )
                FROM ViewStatsModel e
                WHERE e.timestamp >= :start AND e.timestamp <= :end
                GROUP BY e.app, e.uri
                ORDER BY
                    CASE
                        WHEN :unique = TRUE THEN COUNT(DISTINCT e.ip)
                        ELSE COUNT(e.ip)
                    END DESC
            """)
    List<EndpointStatsDto> getStatistics(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("unique") boolean unique);

}
