package ru.practicum.event.service.compilation.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.event.service.compilation.model.Compilation;
import ru.practicum.interaction.api.dto.compilation.CompilationDto;
import ru.practicum.interaction.api.dto.compilation.NewCompilationDto;
import ru.practicum.interaction.api.dto.compilation.UpdateCompilationRequest;
import ru.practicum.event.service.event.mapper.EventMapper;
import ru.practicum.event.service.event.model.Event;
import ru.practicum.event.service.exception.ValidationException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CompilationMapper {
    private final EventMapper eventMapper;

    public CompilationDto mapToCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(eventMapper.mapToEventShortDto(compilation.getEvents()))
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }

    public List<CompilationDto> mapToCompilationDto(Iterable<Compilation> compilations) {
        List<CompilationDto> compilationsResult = new ArrayList<>();

        for (Compilation compilation : compilations) {
            compilationsResult.add(mapToCompilationDto(compilation));
        }

        return compilationsResult.stream()
                .sorted(Comparator.comparing(CompilationDto::getId))
                .toList();
    }

    public Compilation mapFromRequest(NewCompilationDto compilation, List<Event> events) {
        return new Compilation(
                events,
                validatePinned(compilation.getPinned()),
                validateTitle(compilation.getTitle())
        );
    }

    private Boolean validatePinned(Boolean pinned) {
        if (pinned == null) {
            pinned = false;
        }

        return pinned;
    }

    private String validateTitle(String title) {
        if (title.length() > 50) {
            throw new ValidationException("Название подборки не может быть больше 50 символов");
        } else {
            return title;
        }
    }

    public Compilation updateCompilationFields(Compilation compilation, UpdateCompilationRequest compilationFromRequest) {
        if (compilationFromRequest.hasPinned()) {
            compilation.setPinned(compilationFromRequest.getPinned());
        }

        if (compilationFromRequest.hasTitle()) {
            compilation.setTitle(validateTitle(compilationFromRequest.getTitle()));
        }

        return compilation;
    }
}