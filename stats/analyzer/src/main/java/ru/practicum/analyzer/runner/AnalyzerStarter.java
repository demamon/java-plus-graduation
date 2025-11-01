package ru.practicum.analyzer.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.service.EventSimilarityService;
import ru.practicum.analyzer.service.UserActionService;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyzerStarter implements CommandLineRunner {

    private final UserActionService userActionService;
    private final EventSimilarityService eventSimilarityService;

    private ExecutorService executorService;

    @Override
    public void run(String... args) {
        log.info("Запуск Analyzer сервисов...");

        executorService = Executors.newFixedThreadPool(2);

        try {
            executorService.submit(() -> {
                Thread.currentThread().setName("user-action-service");
                log.info("Запуск UserActionService в потоке: {}", Thread.currentThread().getName());
                try {
                    userActionService.run();
                } catch (Exception e) {
                    log.error("Критическая ошибка в UserActionService", e);
                    System.exit(1);
                }
            });

            executorService.submit(() -> {
                Thread.currentThread().setName("event-similarity-service");
                log.info("Запуск EventSimilarityService в потоке: {}", Thread.currentThread().getName());
                try {
                    eventSimilarityService.run();
                } catch (Exception e) {
                    log.error("Критическая ошибка в EventSimilarityService", e);
                    System.exit(1);
                }
            });

            log.info("Оба сервиса analyzer успешно запущены");

        } catch (Exception e) {
            log.error("Ошибка при запуске сервисов analyzer", e);
            shutdown();
            throw e;
        }
    }

    public void shutdown() {
        log.info("Остановка Analyzer сервисов...");

        if (executorService != null) {
            try {
                executorService.shutdown();

                if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                    log.warn("Сервисы не завершились за 30 секунд, принудительная остановка");
                    executorService.shutdownNow();
                }

                log.info("Analyzer сервисы остановлены");
            } catch (InterruptedException e) {
                log.error("Прерывание при остановке сервисов", e);
                Thread.currentThread().interrupt();
                executorService.shutdownNow();
            }
        }
    }
}
