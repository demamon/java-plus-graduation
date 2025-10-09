package ru.practicum.stats.service;

import ru.practicum.dto.HitDto;
import ru.practicum.dto.NewHitDto;
import ru.practicum.dto.ViewDto;
import ru.practicum.stats.controller.StatsParam;

import java.util.List;

public interface StatsService {

    HitDto saveHit(NewHitDto newHit);

    List<ViewDto> getViews(StatsParam param);
}
