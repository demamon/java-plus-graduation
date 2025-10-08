package ru.practicum.ewm.compilation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.param.AdminCompilationParam;
import ru.practicum.ewm.compilation.service.CompilationService;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
public class AdminCompilationController {

    private final CompilationService compilationService;
    private final String compilationPath = "/{comp-id}";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@Valid @RequestBody NewCompilationDto compilation) {
        AdminCompilationParam param = AdminCompilationParam.builder()
                .compilationFromRequest(compilation)
                .build();
        return compilationService.createCompilation(param);
    }

    @PatchMapping(compilationPath)
    public CompilationDto updateCompilation(@PathVariable(name = "comp-id") long compId,
                                            @RequestBody UpdateCompilationRequest compilation) {
        AdminCompilationParam param = AdminCompilationParam.builder()
                .compilationOnUpdate(compilation)
                .compId(compId)
                .build();
        return compilationService.updateCompilation(param);
    }

    @DeleteMapping(compilationPath)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCompilation(@PathVariable(name = "comp-id") long compId) {
        AdminCompilationParam param = AdminCompilationParam.builder()
                .compId(compId)
                .build();
        compilationService.removeCompilation(param);
    }
}