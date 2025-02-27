package ru.yandex.practicum.event.mapper;

import jakarta.annotation.Nonnull;
import ru.yandex.practicum.category.CategoryMapper;
import ru.yandex.practicum.event.dto.EventFullDto;
import ru.yandex.practicum.event.dto.EventShortDto;
import ru.yandex.practicum.event.dto.LocationDto;
import ru.yandex.practicum.event.model.EventModel;
import ru.yandex.practicum.user.UserMapper;

public class EventMapper {

    public static EventFullDto toFullDto(@Nonnull EventModel eventModel) {
        return EventFullDto.builder()
                .id(eventModel.getId())
                .annotation(eventModel.getAnnotation())
                .category(CategoryMapper.toDto(eventModel.getCategory()))
                .createdOn(eventModel.getCreatedOn())
                .description(eventModel.getDescription())
                .eventDate(eventModel.getEventDate())
                .initiator(UserMapper.toShortDto(eventModel.getInitiator()))
                .location(new LocationDto(eventModel.getLocationLat(), eventModel.getLocationLon()))
                .paid(eventModel.getPaid())
                .participantLimit(eventModel.getParticipantLimit())
                .publishedOn(eventModel.getPublishedOn())
                .requestModeration(eventModel.getRequestModeration())
                .state(eventModel.getState())
                .title(eventModel.getTitle())
                .build();
    }

    public static EventShortDto toShortDto(@Nonnull EventModel eventModel) {
        return EventShortDto.builder()
                .id(eventModel.getId())
                .annotation(eventModel.getAnnotation())
                .category(CategoryMapper.toDto(eventModel.getCategory()))
                .eventDate(eventModel.getEventDate())
                .initiator(UserMapper.toShortDto(eventModel.getInitiator()))
                .paid(eventModel.getPaid())
                .title(eventModel.getTitle())
                .build();
    }
}
