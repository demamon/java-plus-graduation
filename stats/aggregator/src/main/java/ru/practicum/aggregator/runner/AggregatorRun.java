package ru.practicum.aggregator.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.practicum.aggregator.service.AggregatorService;


@Slf4j
@Component
@RequiredArgsConstructor
public class AggregatorRun implements CommandLineRunner {

    private final AggregatorService aggregatorService;

    @Override
    public void run(String... args) {
        log.info("Запуск AggregatorService...");
        try {
            aggregatorService.run();
        } catch (Exception e) {
            log.error("Критическая ошибка в AggregatorService", e);
            System.exit(1);
        }
    }
}
