package ru.yandex.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.compilation.CompilationMapper;
import ru.yandex.practicum.compilation.CompilationRepository;
import ru.yandex.practicum.compilation.dto.CompilationDto;
import ru.yandex.practicum.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationPublicService {

    private final CompilationRepository compilationRepository;

    public List<CompilationDto> findCompilations(int from, int size, Boolean pinned) {
        if (pinned == null) {
            return compilationRepository.findAll(PageRequest.of(from, size)).stream().map(CompilationMapper::toDto).toList();
        } else {
            return compilationRepository.findAllByPinned(pinned, PageRequest.of(from, size)).stream().map(CompilationMapper::toDto).toList();
        }
    }

    public CompilationDto findById(long compId) {
        return CompilationMapper.toDto(compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException(
                "Compilation with id=%d was not found".formatted(compId)
        )));
    }
}
