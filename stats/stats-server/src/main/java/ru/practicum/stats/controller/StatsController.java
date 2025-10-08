package ru.practicum.stats.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.NewHitDto;
import ru.practicum.dto.ViewDto;
import ru.practicum.stats.service.StatsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public HitDto saveHit(@Valid @RequestBody NewHitDto newHit) {
        log.info("получаем запрос на сохранение просмотров {}", newHit);
        return statsService.saveHit(newHit);
    }

    @GetMapping("/stats")
    public List<ViewDto> getViews(@RequestParam String start,
                                  @RequestParam String end,
                                  @RequestParam(required = false) List<String> uris,
                                  @RequestParam(defaultValue = "false") boolean unique) {
        StatsParam param = StatsParam.builder()
                .start(start)
                .end(end)
                .uris(uris)
                .unique(unique)
                .build();
        return statsService.getViews(param);
    }
}
