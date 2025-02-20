package ru.yandex.practicum.service;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.EndpointHitDto;
import ru.yandex.practicum.EndpointStatsDto;
import ru.yandex.practicum.mapper.ViewStatsMapper;
import ru.yandex.practicum.model.ViewStatsModel;
import ru.yandex.practicum.repository.EwmStatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EwmStatsService {
    private final EwmStatsRepository ewmStatsRepository;

    public void hit(@Nonnull EndpointHitDto endpointHitDto) {
        // Переводк Entity
        ViewStatsModel viewStatsModel = ViewStatsMapper.toViewStats(endpointHitDto);

        // сохранение логов в БД
        ewmStatsRepository.save(viewStatsModel);
    }

    public List<EndpointStatsDto> stats(@Nonnull LocalDateTime start, @Nonnull LocalDateTime end, List<String> uris, boolean unique) {

        // Получаем список статистики
        List<EndpointStatsDto> endpointStatsDtoList = ewmStatsRepository.getStatistics(start, end, unique);

        // Если присутствуе uris, выбираем только что указаны
        if (uris != null) {
            endpointStatsDtoList = endpointStatsDtoList.stream().filter(stats -> uris.contains(stats.getUri())).toList();
        }

        return endpointStatsDtoList;
    }

}
