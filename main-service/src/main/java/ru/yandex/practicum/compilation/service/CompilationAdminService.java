package ru.yandex.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.compilation.CompilationMapper;
import ru.yandex.practicum.compilation.CompilationModel;
import ru.yandex.practicum.compilation.CompilationRepository;
import ru.yandex.practicum.compilation.dto.CompilationDto;
import ru.yandex.practicum.compilation.dto.UpdateCompilationDto;
import ru.yandex.practicum.event.model.EventModel;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.exception.NotFoundException;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationAdminService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    public CompilationDto createNewCompilation(UpdateCompilationDto updateCompilationDto) {
        CompilationModel compilationModel = new CompilationModel();
        compilationModel.setTitle(updateCompilationDto.getTitle());
        compilationModel.setPinned(updateCompilationDto.getPinned());

        if (updateCompilationDto.getEvents() != null) {
            List<EventModel> events = eventRepository.findAllById(updateCompilationDto.getEvents());
            compilationModel.setEvents(new HashSet<>(events));
        }

        return CompilationMapper.toDto(compilationRepository.save(compilationModel));
    }

    public void deleteCompilation(long compId) {
        compilationRepository.deleteById(compId);
    }

    public CompilationDto updateCompilation(long compId, UpdateCompilationDto updateCompilationDto) {
        CompilationModel compilationModel = compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException(
                "Compilation with id=%d was not found".formatted(compId)
        ));

        if (updateCompilationDto.getEvents() != null) {
            List<EventModel> events = eventRepository.findAllById(updateCompilationDto.getEvents());
            compilationModel.setEvents(new HashSet<>(events));
        }

        if (updateCompilationDto.getPinned() != null) {
            compilationModel.setPinned(updateCompilationDto.getPinned());
        }

        if (updateCompilationDto.getTitle() != null) {
            compilationModel.setTitle(updateCompilationDto.getTitle());
        }

        return CompilationMapper.toDto(compilationRepository.save(compilationModel));
    }
}
