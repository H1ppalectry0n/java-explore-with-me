package ru.yandex.practicum.compilation;

import jakarta.annotation.Nonnull;
import ru.yandex.practicum.compilation.dto.CompilationDto;
import ru.yandex.practicum.event.mapper.EventMapper;

public class CompilationMapper {

    public static CompilationDto toDto(@Nonnull CompilationModel compilationModel) {
        return CompilationDto.builder()
                .id(compilationModel.getId())
                .events(compilationModel.getEvents().stream().map(EventMapper::toShortDto).toList())
                .title(compilationModel.getTitle())
                .pinned(compilationModel.getPinned())
                .build();
    }
}
