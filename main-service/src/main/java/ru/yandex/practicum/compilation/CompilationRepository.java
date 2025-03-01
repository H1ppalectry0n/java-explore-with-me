package ru.yandex.practicum.compilation;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompilationRepository extends JpaRepository<CompilationModel, Long> {

    List<CompilationModel> findAllByPinned(boolean pinned, Pageable pageable);
}
