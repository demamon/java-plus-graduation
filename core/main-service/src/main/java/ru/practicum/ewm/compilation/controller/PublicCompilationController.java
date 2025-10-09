package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.param.PublicCompilationParam;
import ru.practicum.ewm.compilation.service.CompilationService;

import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
public class PublicCompilationController {

    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {
        PublicCompilationParam param = PublicCompilationParam.builder()
                .pinned(pinned)
                .from(from)
                .size(size)
                .build();
        return compilationService.getCompilations(param);
    }

    @GetMapping("/{comp-id}")
    public CompilationDto getCompilationById(@PathVariable(name = "comp-id") long compId) {
        PublicCompilationParam param = PublicCompilationParam.builder()
                .compId(compId)
                .build();
        return compilationService.getCompilationById(param);
    }
}