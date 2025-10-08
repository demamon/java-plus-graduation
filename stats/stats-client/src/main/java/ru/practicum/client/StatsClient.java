package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.NewHitDto;
import ru.practicum.dto.ViewDto;

import java.util.List;

@FeignClient(name = "stats-server") // Используем имя сервиса из Eureka
public interface StatsClient {

    @PostMapping("/hit")
    void registerHit(@RequestBody NewHitDto hitDto);

    @GetMapping("/stats")
    List<ViewDto> getStats(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam List<String> uris,
            @RequestParam boolean unique
    );
}
