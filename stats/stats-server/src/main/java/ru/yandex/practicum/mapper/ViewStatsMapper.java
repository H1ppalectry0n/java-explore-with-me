package ru.yandex.practicum.mapper;

import jakarta.annotation.Nonnull;
import ru.yandex.practicum.EndpointHitDto;
import ru.yandex.practicum.model.ViewStatsModel;

public class ViewStatsMapper {
    public static ViewStatsModel toViewStats(@Nonnull EndpointHitDto endpointHitDto) {
        ViewStatsModel viewStatsModel = new ViewStatsModel();
        viewStatsModel.setApp(endpointHitDto.getApp());
        viewStatsModel.setUri(endpointHitDto.getUri());
        viewStatsModel.setIp(endpointHitDto.getIp());
        viewStatsModel.setTimestamp(endpointHitDto.getTimestamp());
        return viewStatsModel;
    }
}
