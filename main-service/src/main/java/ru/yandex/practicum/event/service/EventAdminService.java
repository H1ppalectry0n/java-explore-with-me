package ru.yandex.practicum.event.service;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.category.CategoryModel;
import ru.yandex.practicum.category.CategoryRepository;
import ru.yandex.practicum.event.dto.EventFullDto;
import ru.yandex.practicum.event.dto.UpdateEventAdminDto;
import ru.yandex.practicum.event.mapper.EventMapper;
import ru.yandex.practicum.event.model.EventModel;
import ru.yandex.practicum.event.model.EventState;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.event.repository.EventSpecification;
import ru.yandex.practicum.exception.ConstraintException;
import ru.yandex.practicum.exception.ForbiddenException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.request.model.ParticipantCount;
import ru.yandex.practicum.request.model.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventAdminService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;

    public List<EventFullDto> findAllWithFilters(int from, int size, List<Long> userIds, List<EventState> states,
                                                 List<Long> categoryIds, LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        // Создание фильтра
        Specification<EventModel> spec = EventSpecification.withFiltersForAdmin(userIds, states, categoryIds, rangeStart, rangeEnd);

        // Получение списка событий
        Page<EventModel> events = eventRepository.findAll(spec, PageRequest.of(from, size));

        Map<Long, Integer> participantsCount = requestRepository.findParticipantCount(events.stream().map(EventModel::getId).toList())
                .stream().collect(Collectors.toMap(ParticipantCount::getEventId, ParticipantCount::getParticipantCount));

        // Возвращение списка DTO
        return events.stream().map(EventMapper::toFullDto)
                .peek(e -> e.setConfirmedRequests(participantsCount.getOrDefault(e.getId(), 0)))
                .toList();
    }

    @Transactional
    public EventFullDto patchEventWithId(long eventId, @Nonnull UpdateEventAdminDto updateEventDto) {
        EventModel event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                "Event with id=%d was not found".formatted(eventId)
        ));

        if (updateEventDto.getEventDate() != null) {
            if (updateEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                throw new ForbiddenException("Cannot publish the event because it's not in the right state: EVENT_DATE");
            }

            event.setEventDate(updateEventDto.getEventDate());
        }

        if (updateEventDto.getStateAction() != null) {
            if (event.getState() != EventState.PENDING) {
                throw new ConstraintException("Cannot publish the event because it's not in the right state: PUBLISHED");
            }

            switch (updateEventDto.getStateAction()) {
                case REJECT_EVENT -> event.setState(EventState.CANCELED);
                case PUBLISH_EVENT -> {
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                }
            }
        }

        if (updateEventDto.getCategory() != null) {
            CategoryModel category = categoryRepository.findById(updateEventDto.getCategory()).orElseThrow(() ->
                    new NotFoundException("Category with id=%d was not found".formatted(updateEventDto.getCategory()))
            );

            event.setCategory(category);
        }

        if (updateEventDto.getAnnotation() != null) {
            event.setAnnotation(updateEventDto.getAnnotation());
        }

        if (updateEventDto.getDescription() != null) {
            event.setDescription(updateEventDto.getDescription());
        }

        if (updateEventDto.getLocation() != null) {
            event.setLocationLat(updateEventDto.getLocation().getLat());
            event.setLocationLon(updateEventDto.getLocation().getLon());
        }

        if (updateEventDto.getPaid() != null) {
            event.setPaid(updateEventDto.getPaid());
        }

        if (updateEventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventDto.getParticipantLimit());
        }

        if (updateEventDto.getRequestModeration() != null) {
            event.setRequestModeration(updateEventDto.getRequestModeration());
        }

        if (updateEventDto.getTitle() != null) {
            event.setTitle(updateEventDto.getTitle());
        }

        return EventMapper.toFullDto(eventRepository.save(event));
    }
}
