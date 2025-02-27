package ru.yandex.practicum.request;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.event.model.EventModel;
import ru.yandex.practicum.event.model.EventState;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.exception.ConstraintException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.request.dto.ParticipationRequestDto;
import ru.yandex.practicum.request.model.RequestRepository;
import ru.yandex.practicum.user.UserModel;
import ru.yandex.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestPrivateService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public List<ParticipationRequestDto> findAllByUserId(long userId) {
        UserModel user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id=%d was not found".formatted(userId)
        ));

        return requestRepository.findAllByRequester(user).stream().map(RequestMapper::toDto).toList();
    }

    public ParticipationRequestDto createNewRequest(long userId, long eventId) {
        UserModel user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id=%d was not found".formatted(userId)
        ));

        EventModel event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                "Event with id=%d was not found".formatted(eventId)
        ));

        if (event.getInitiator().equals(user)) {
            throw new ConstraintException("Event initiator same as user");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConstraintException("Event doesn't published");
        }

        if (event.getParticipantLimit() != 0 && requestRepository.countByEventIdAndRequestStatus(eventId, RequestStatus.CONFIRMED) == event.getParticipantLimit()) {
            throw new ConstraintException("Participant limit");
        }

        RequestModel request = new RequestModel();
        request.setRequester(user);
        request.setEvent(event);
        request.setRequestStatus((!event.getRequestModeration() || event.getParticipantLimit() == 0) ? RequestStatus.CONFIRMED : RequestStatus.PENDING);
        request.setCreated(LocalDateTime.now());

        try {
            return RequestMapper.toDto(requestRepository.save(request));
        } catch (DataIntegrityViolationException e) {
            throw new ConstraintException(e.getMessage());
        }
    }

    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        UserModel user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id=%d was not found".formatted(userId)
        ));

        RequestModel request = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException(
                "Request with id=%d was not found".formatted(requestId)
        ));

        request.setRequestStatus(RequestStatus.CANCELED);

        return RequestMapper.toDto(requestRepository.save(request));
    }
}
