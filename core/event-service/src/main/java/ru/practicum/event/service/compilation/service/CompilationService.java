package ru.practicum.event.service.compilation.service;

import ru.practicum.interaction.api.dto.compilation.AdminCompilationParam;
import ru.practicum.interaction.api.dto.compilation.PublicCompilationParam;
import ru.practicum.interaction.api.dto.compilation.CompilationDto;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> getCompilations(PublicCompilationParam param);

    CompilationDto getCompilationById(PublicCompilationParam param);

    CompilationDto createCompilation(AdminCompilationParam param);

    CompilationDto updateCompilation(AdminCompilationParam param);

    void removeCompilation(AdminCompilationParam param);
}
