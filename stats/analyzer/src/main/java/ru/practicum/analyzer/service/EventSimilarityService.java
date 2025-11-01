package ru.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.handler.EventSimilarityHandler;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventSimilarityService implements Runnable {

    private final Consumer<Long, EventSimilarityAvro> consumer;
    private final EventSimilarityHandler eventSimilarityHandler;

    @Value("${analyzer.topic.events-similarity}")
    private String topicEventSimilarity;

    @Value("${spring.kafka.consumer.poll-timeout}")
    private int pollTimeout;

    private final AtomicBoolean running = new AtomicBoolean(false);

    public void run() {
        if (!running.compareAndSet(false, true)) {
            log.warn("EventSimilarityService уже запущен");
            return;
        }

        try {
            log.info("Запуск EventSimilarityService. Подписка на топик: {}", topicEventSimilarity);
            consumer.subscribe(List.of(topicEventSimilarity));

            Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

            log.info("Начинаем чтение сообщений из Kafka...");

            while (running.get()) {
                try {
                    ConsumerRecords<Long, EventSimilarityAvro> records = consumer.poll(Duration.ofMillis(pollTimeout));

                    if (records.isEmpty()) {
                        continue;
                    }

                    log.info("Получено {} сообщений о схожести событий", records.count());
                    processRecords(records);

                    consumer.commitAsync();

                } catch (WakeupException e) {
                    if (running.get()) {
                        log.warn("Получен WakeupException, но сервис еще работает");
                        continue;
                    }
                    break;
                } catch (Exception e) {
                    log.error("Ошибка при опросе Kafka топика {}", topicEventSimilarity, e);
                    sleep(5000);
                }
            }

        } catch (Exception e) {
            log.error("Критическая ошибка в EventSimilarityService", e);
            throw e;
        } finally {
            shutdown();
        }
    }

    private void processRecords(ConsumerRecords<Long, EventSimilarityAvro> records) {
        int processedCount = 0;
        int errorCount = 0;

        for (ConsumerRecord<Long, EventSimilarityAvro> record : records) {
            try {
                EventSimilarityAvro eventSimilarity = record.value();
                log.debug("Обработка схожести: ключ={}, смещение={}, partition={}",
                        record.key(), record.offset(), record.partition());

                eventSimilarityHandler.handle(eventSimilarity);
                processedCount++;

            } catch (Exception e) {
                errorCount++;
                log.error("Ошибка обработки сообщения: ключ={}, offset={}",
                        record.key(), record.offset(), e);
            }
        }

        log.info("Обработка завершена: успешно={}, с ошибками={}", processedCount, errorCount);
    }

    public void shutdown() {
        if (running.compareAndSet(true, false)) {
            log.info("Запущен graceful shutdown EventSimilarityService...");

            try {
                consumer.wakeup(); // Прерываем текущий poll
            } catch (Exception e) {
                log.warn("Ошибка при wakeup consumer", e);
            }

            try {
                consumer.commitSync();
                log.info("Смещения закоммичены");
            } catch (Exception e) {
                log.error("Ошибка при коммите смещений", e);
            }

            try {
                consumer.close();
                log.info("Consumer закрыт");
            } catch (Exception e) {
                log.error("Ошибка при закрытии consumer", e);
            }

            log.info("EventSimilarityService остановлен");
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Поток прерван во время сна");
        }
    }

    public boolean isRunning() {
        return running.get();
    }
}
