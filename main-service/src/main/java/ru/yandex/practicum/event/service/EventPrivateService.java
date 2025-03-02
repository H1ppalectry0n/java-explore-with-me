package ru.yandex.practicum.event.service;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.category.CategoryModel;
import ru.yandex.practicum.category.CategoryRepository;
import ru.yandex.practicum.event.dto.EventFullDto;
import ru.yandex.practicum.event.dto.NewEventDto;
import ru.yandex.practicum.event.dto.UpdateEventUserDto;
import ru.yandex.practicum.event.mapper.EventMapper;
import ru.yandex.practicum.event.model.EventModel;
import ru.yandex.practicum.event.model.EventState;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.exception.ConstraintException;
import ru.yandex.practicum.exception.ForbiddenException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.request.RequestMapper;
import ru.yandex.practicum.request.RequestModel;
import ru.yandex.practicum.request.RequestStatus;
import ru.yandex.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.yandex.practicum.request.dto.ParticipationRequestDto;
import ru.yandex.practicum.request.model.RequestRepository;
import ru.yandex.practicum.user.UserModel;
import ru.yandex.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EventPrivateService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;

    private static final int HOURS_TO_PUBLISH = 2;

    @Transactional(readOnly = true)
    public List<EventFullDto> findByUserId(long userId, int from, int size) {
        UserModel user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id=%d was not found".formatted(userId)
        ));

        return eventRepository.findAllByInitiator(user, PageRequest.of(from, size)).stream()
                .map(EventMapper::toFullDto)
                .toList();
    }

    public EventFullDto postNewEvent(long userId, @Nonnull NewEventDto newEvent) {
        UserModel user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id=%d was not found".formatted(userId)
        ));

        CategoryModel category = categoryRepository.findById(newEvent.getCategory()).orElseThrow(() -> new NotFoundException(
                "Category with id=%d was not found".formatted(newEvent.getCategory())
        ));

        if (newEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(HOURS_TO_PUBLISH))) {
            throw new ForbiddenException("Field: eventDate. Error: должно содержать дату, которая еще не наступила.");
        }

        EventModel event = EventModel.builder()
                .annotation(newEvent.getAnnotation())
                .category(category)
                .description(newEvent.getDescription())
                .eventDate(newEvent.getEventDate())
                .createdOn(LocalDateTime.now())
                .locationLat(newEvent.getLocation().getLat())
                .locationLon(newEvent.getLocation().getLon())
                .paid(newEvent.getPaid())
                .participantLimit(newEvent.getParticipantLimit())
                .requestModeration(newEvent.getRequestModeration())
                .title(newEvent.getTitle())
                .initiator(user)
                .state(EventState.PENDING)
                .build();

        return EventMapper.toFullDto(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    public EventFullDto findByEventIdForUser(long userId, long eventId) {
        UserModel user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id=%d was not found".formatted(userId)
        ));

        return EventMapper.toFullDto(eventRepository.findByIdAndInitiator(eventId, user).orElseThrow(() -> new NotFoundException(
                "Event with id=%d was not found".formatted(eventId)
        )));
    }

    public EventFullDto updateEvent(long userId, long eventId, UpdateEventUserDto updatedEvent) {
        UserModel user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id=%d was not found".formatted(userId)
        ));

        EventModel event = eventRepository.findByIdAndInitiator(eventId, user).orElseThrow(() -> new NotFoundException(
                "Event with id=%d was not found".formatted(eventId)
        ));

        if (event.getState() == EventState.PUBLISHED) {
            throw new ConstraintException("Only pending or canceled events can be changed");
        }

        if (updatedEvent.getEventDate() != null) {
            if (updatedEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(HOURS_TO_PUBLISH))) {
                throw new ForbiddenException("Field: eventDate. Error: должно содержать дату, которая еще не наступила.");
            }

            event.setEventDate(updatedEvent.getEventDate());
        }

        if (updatedEvent.getCategory() != null) {
            CategoryModel category = categoryRepository.findById(updatedEvent.getCategory()).orElseThrow(() -> new NotFoundException(
                    "Category with id=%d was not found".formatted(updatedEvent.getCategory())
            ));

            event.setCategory(category);
        }

        if (updatedEvent.getDescription() != null) {
            event.setDescription(updatedEvent.getDescription());
        }

        if (updatedEvent.getAnnotation() != null) {
            event.setAnnotation(updatedEvent.getAnnotation());
        }

        if (updatedEvent.getLocation() != null) {
            event.setLocationLat(updatedEvent.getLocation().getLat());
            event.setLocationLon(updatedEvent.getLocation().getLon());
        }

        if (updatedEvent.getPaid() != null) {
            event.setPaid(updatedEvent.getPaid());
        }

        if (updatedEvent.getParticipantLimit() != null) {
            event.setParticipantLimit(updatedEvent.getParticipantLimit());
        }

        if (updatedEvent.getRequestModeration() != null) {
            event.setRequestModeration(updatedEvent.getRequestModeration());
        }

        if (updatedEvent.getStateAction() != null) {
            switch (updatedEvent.getStateAction()) {
                case CANCEL_REVIEW -> event.setState(EventState.CANCELED);
                case SEND_TO_REVIEW -> event.setState(EventState.PENDING);
            }
        }

        if (updatedEvent.getTitle() != null) {
            event.setTitle(updatedEvent.getTitle());
        }

        return EventMapper.toFullDto(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> findAllRequestsForEvent(long userId, long eventId) {
        UserModel user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id=%d was not found".formatted(userId)
        ));

        EventModel event = eventRepository.findByIdAndInitiator(eventId, user).orElseThrow(() -> new NotFoundException(
                "Event with id=%d was not found".formatted(eventId)
        ));

        return requestRepository.findAllByEvent(event).stream().map(RequestMapper::toDto).toList();
    }

    public EventRequestStatusUpdateResult updateRequests(long userId, long eventId, @Nonnull EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        UserModel user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id=%d was not found".formatted(userId)
        ));

        EventModel event = eventRepository.findByIdAndInitiator(eventId, user).orElseThrow(() -> new NotFoundException(
                "Event with id=%d was not found".formatted(eventId)
        ));

        List<RequestModel> requests = confirmOrRejectRequests(event, eventRequestStatusUpdateRequest);

        // Сохранение изменений
        requestRepository.saveAll(requests);

        List<ParticipationRequestDto> confirmedRequests = requests.stream().filter(r -> r.getRequestStatus() == RequestStatus.CONFIRMED &&
                eventRequestStatusUpdateRequest.getRequestIds().contains(r.getId())).map(RequestMapper::toDto).toList();
        List<ParticipationRequestDto> rejectedRequests = requests.stream().filter(r -> r.getRequestStatus() == RequestStatus.REJECTED &&
                eventRequestStatusUpdateRequest.getRequestIds().contains(r.getId())).map(RequestMapper::toDto).toList();

        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    private List<RequestModel> confirmOrRejectRequests(@Nonnull EventModel event, @Nonnull EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        // Получение всех заявок для события
        List<RequestModel> requests = requestRepository.findAllByEvent(event);

        if (eventRequestStatusUpdateRequest.getStatus() == RequestStatus.CONFIRMED) {
            long confirmedCount = requests.stream().filter(r -> r.getRequestStatus() == RequestStatus.CONFIRMED).count();
            long availablePositions = event.getParticipantLimit() - confirmedCount;

            if (availablePositions < eventRequestStatusUpdateRequest.getRequestIds().size() && event.getParticipantLimit() != 0) {
                throw new ConstraintException("достигнут лимит");
            }

            // Подтвераем всех кому осталось место
            for (int i = 0; i < requests.size() && (availablePositions > 0 || event.getParticipantLimit() == 0); i++) {
                RequestModel request = requests.get(i);
                if (request.getRequestStatus() != RequestStatus.CONFIRMED &&
                        eventRequestStatusUpdateRequest.getRequestIds().contains(request.getId())) {
                    request.setRequestStatus(RequestStatus.CONFIRMED);
                    availablePositions--;
                }
            }

            // Если достигнут лимит отклонить все не подтвержденные заявки
            if (availablePositions == 0 && event.getParticipantLimit() != 0) {
                for (RequestModel request : requests) {
                    if (request.getRequestStatus() == RequestStatus.PENDING) {
                        request.setRequestStatus(RequestStatus.REJECTED);
                    }
                }
            }
        } else {
            for (RequestModel request : requests) {
                if (eventRequestStatusUpdateRequest.getRequestIds().contains(request.getId())) {
                    if (request.getRequestStatus() == RequestStatus.CONFIRMED) {
                        throw new ConstraintException("Заявка уже прнята");
                    }
                    request.setRequestStatus(RequestStatus.REJECTED);
                }
            }
        }

        return requests;
    }
}
