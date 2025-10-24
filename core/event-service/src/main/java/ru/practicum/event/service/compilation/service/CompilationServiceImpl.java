package ru.practicum.event.service.compilation.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.service.compilation.model.Compilation;
import ru.practicum.event.service.compilation.mapper.CompilationMapper;
import ru.practicum.event.service.compilation.model.QCompilation;
import ru.practicum.event.service.compilation.repository.CompilationRepository;
import ru.practicum.interaction.api.dto.compilation.CompilationDto;
import ru.practicum.interaction.api.dto.compilation.NewCompilationDto;
import ru.practicum.interaction.api.dto.compilation.UpdateCompilationRequest;
import ru.practicum.interaction.api.dto.compilation.AdminCompilationParam;
import ru.practicum.interaction.api.dto.compilation.PublicCompilationParam;
import ru.practicum.event.service.event.model.Event;
import ru.practicum.event.service.event.repository.EventRepository;
import ru.practicum.event.service.exception.ConflictException;
import ru.practicum.event.service.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;

    @Override
    public List<CompilationDto> getCompilations(PublicCompilationParam param) {
        QCompilation qCompilation = QCompilation.compilation;
        List<BooleanExpression> conditions = new ArrayList<>();

        Pageable page = PageRequest.of(param.getFrom(), param.getSize());

        if (param.getPinned() != null) {
            conditions.add(QCompilation.compilation.pinned.eq(param.getPinned()));

            BooleanExpression finalCondition = conditions.stream()
                    .reduce(BooleanExpression::and)
                    .get();

            return compilationMapper.mapToCompilationDto(compilationRepository.findAll(finalCondition, page));
        } else {
            return compilationMapper.mapToCompilationDto(compilationRepository.findAll(page));
        }
    }

    @Override
    public CompilationDto getCompilationById(PublicCompilationParam param) {
        long compId = param.getCompId();
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException(String.format("Подборка id = %d не найдена", compId))
        );
        return compilationMapper.mapToCompilationDto(compilation);
    }

    @Transactional
    @Override
    public CompilationDto createCompilation(AdminCompilationParam param) {
        NewCompilationDto compilationRequest = param.getCompilationFromRequest();
        List<Event> events = new ArrayList<>();

        if (compilationRequest.hasEvents()) {
            for (Long eventId : compilationRequest.getEvents()) {
                Event event = eventRepository.findById(eventId).orElseThrow(
                        () -> new ConflictException(String.format("Событие id = %d не найдено", eventId))
                );
                events.add(event);
            }
        }

        Compilation newCompilation = compilationRepository.save(
                compilationMapper.mapFromRequest(compilationRequest, events)
        );
        return compilationMapper.mapToCompilationDto(newCompilation);
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(AdminCompilationParam param) {
        long compId = param.getCompId();
        UpdateCompilationRequest compilationRequest = param.getCompilationOnUpdate();
        Compilation oldCompilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException(String.format("Подборка id = %d не найдена", compId))
        );
        List<Event> events = oldCompilation.getEvents();

        if (compilationRequest.hasEvents()) {
            for (Long eventId : compilationRequest.getEvents()) {
                Event event = eventRepository.findById(eventId).orElseThrow(
                        () -> new ConflictException(String.format("Событие id = %d не найдено", eventId))
                );
                events.add(event);
            }
            oldCompilation.setEvents(events);
        }
        Compilation updatedCompilation = compilationRepository.save(
                compilationMapper.updateCompilationFields(oldCompilation, compilationRequest)
        );
        return compilationMapper.mapToCompilationDto(updatedCompilation);
    }

    @Transactional
    @Override
    public void removeCompilation(AdminCompilationParam param) {
        long compId = param.getCompId();
        if (compilationRepository.findById(compId).isEmpty())
            throw new NotFoundException(String.format("Подборка id = %d не найдена", compId));
        compilationRepository.deleteById(compId);
    }


}
