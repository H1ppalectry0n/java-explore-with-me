package ru.yandex.practicum.event.service;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.event.dto.EventFullDto;
import ru.yandex.practicum.event.dto.EventSortType;
import ru.yandex.practicum.event.mapper.EventMapper;
import ru.yandex.practicum.event.model.EventModel;
import ru.yandex.practicum.event.model.EventState;
import ru.yandex.practicum.event.model.EventViewModel;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.event.repository.EventSpecification;
import ru.yandex.practicum.event.repository.EventViewRepository;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.request.model.ParticipantCount;
import ru.yandex.practicum.request.model.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventPublicService {

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final EventViewRepository eventViewRepository;

    public List<EventFullDto> findAllWithFilters(int from, int size, String userIp, String text, List<Long> categoryIds, Boolean paid,
                                                 LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, EventSortType sortType) {
        // Создание фильтра
        Specification<EventModel> spec = EventSpecification.withFiltersForPublic(categoryIds, paid, rangeStart == null ? LocalDateTime.now() : rangeStart, rangeEnd);

        List<EventModel> events;

        if (sortType != null) {
            Sort sort = getSortTypeForQuery(sortType);
            events = eventRepository.findAll(spec, PageRequest.of(from, size, sort)).stream().toList();
        } else {
            events = eventRepository.findAll(spec, PageRequest.of(from, size)).stream().toList();
        }

        // Только те где не исчерпан лимит.
        events = filterByParticipantLimit(events);

        // сортировка по тексту
        if (text != null) {
            events = events.stream().filter(e -> e.getAnnotation().contains(text) || e.getDescription().contains(text)).toList();
        }

        // Сохранение просмотров
        for (EventModel event : events) {
            EventViewModel eventViewModel = new EventViewModel();
            eventViewModel.setEvent(event);
            eventViewModel.setUserIp(userIp);
            eventViewRepository.save(eventViewModel);
        }

        Map<Long, Integer> participantsCount = requestRepository.findParticipantCount(events.stream().map(EventModel::getId).toList())
                .stream().collect(Collectors.toMap(ParticipantCount::getEventId, ParticipantCount::getParticipantCount));


        return events.stream().map(EventMapper::toFullDto)
                .peek(e -> e.setConfirmedRequests(participantsCount.getOrDefault(e.getId(), 0)))
                .peek(e -> e.setViews(eventViewRepository.countUserIpDistinctByEventId(e.getId()))).toList();
    }

    public EventFullDto findById(long eventId, String userIp) {
        Optional<EventModel> event = eventRepository.findById(eventId);

        if (event.isEmpty() || event.get().getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event with id=%d was not found".formatted(eventId));
        }

        EventViewModel eventViewModel = new EventViewModel();
        eventViewModel.setEvent(event.get());
        eventViewModel.setUserIp(userIp);
        eventViewRepository.save(eventViewModel);

        EventFullDto eventFullDto = EventMapper.toFullDto(event.get());
        eventFullDto.setViews(eventViewRepository.countUserIpDistinctByEventId(eventId));

        return eventFullDto;
    }

    private Sort getSortTypeForQuery(@Nonnull EventSortType eventSortType) {
        return switch (eventSortType) {
            case EVENT_DATE -> Sort.by(Sort.Direction.ASC, "eventDate");
            case VIEWS -> Sort.by(Sort.Direction.DESC, "views");
        };
    }

    private List<EventModel> filterByParticipantLimit(List<EventModel> events) {
        // Получение количества участников для каждого события
        Map<Long, Integer> participantCountList = requestRepository.findParticipantCount(events.stream().map(EventModel::getId).toList())
                .stream().collect(Collectors.toMap(
                        ParticipantCount::getEventId,
                        ParticipantCount::getParticipantCount
                ));

        // Только те где не исчерпан лимит.
        return events = events.stream().filter(e -> {
            if (e.getParticipantLimit() == 0 || !participantCountList.containsKey(e.getId())) {
                return true;
            }

            return participantCountList.get(e.getId()) < e.getParticipantLimit();
        }).toList();
    }
}
