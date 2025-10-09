package ru.practicum.ewm.compilation.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.Compilation;
import ru.practicum.ewm.compilation.CompilationMapper;
import ru.practicum.ewm.compilation.CompilationRepository;
import ru.practicum.ewm.compilation.QCompilation;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.param.AdminCompilationParam;
import ru.practicum.ewm.compilation.param.PublicCompilationParam;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

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

            return CompilationMapper.mapToCompilationDto(compilationRepository.findAll(finalCondition, page));
        } else {
            return CompilationMapper.mapToCompilationDto(compilationRepository.findAll(page));
        }
    }

    @Override
    public CompilationDto getCompilationById(PublicCompilationParam param) {
        long compId = param.getCompId();
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException(String.format("Подборка id = %d не найдена", compId))
        );
        return CompilationMapper.mapToCompilationDto(compilation);
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
                CompilationMapper.mapFromRequest(compilationRequest, events)
        );
        return CompilationMapper.mapToCompilationDto(newCompilation);
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
                CompilationMapper.updateCompilationFields(oldCompilation, compilationRequest)
        );
        return CompilationMapper.mapToCompilationDto(updatedCompilation);
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
