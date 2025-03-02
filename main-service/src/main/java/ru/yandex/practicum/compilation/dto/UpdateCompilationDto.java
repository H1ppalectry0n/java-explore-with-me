package ru.yandex.practicum.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCompilationDto {

    private Set<Long> events;

    @NotBlank(groups = OnCreateCompilationValidation.class)
    @Size(min = 1, max = 50, groups = {OnCreateCompilationValidation.class, OnUpdateCompilationValidation.class})
    private String title;

    private Boolean pinned = false;
}
