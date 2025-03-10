package ru.yandex.practicum.comments.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.comments.dto.CommentShortDto;
import ru.yandex.practicum.comments.mapper.CommentMapper;
import ru.yandex.practicum.comments.model.CommentStatus;
import ru.yandex.practicum.comments.repository.CommentRepository;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentPublicService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;

    public List<CommentShortDto> findAll(long eventId, int from, int size) {
        eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                "Event with id=%d was not found".formatted(eventId)
        ));

        return commentRepository.findAllByEventIdAndStatus(eventId, CommentStatus.APPROVED, PageRequest.of(from, size)).stream()
                .map(CommentMapper::toShortDto)
                .toList();
    }
}
