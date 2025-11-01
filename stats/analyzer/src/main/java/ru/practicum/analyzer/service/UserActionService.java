package ru.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.handler.UserActionHandler;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActionService implements Runnable {

    private final Consumer<Long, UserActionAvro> consumer;
    private final UserActionHandler userActionHandler;

    @Value("${analyzer.topic.user-action}")
    private String topicUserAction;

    @Value("${spring.kafka.consumer.poll-timeout}")
    private int pollTimeout;

    private final AtomicBoolean running = new AtomicBoolean(false);

    @Override
    public void run() {
        if (!running.compareAndSet(false, true)) {
            log.warn("UserActionService уже запущен");
            return;
        }

        try {
            log.info("Запуск UserActionService. Подписка на топик: {}", topicUserAction);
            consumer.subscribe(List.of(topicUserAction));

            Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

            log.info("Начинаем чтение действий пользователей из Kafka...");

            while (running.get()) {
                try {
                    ConsumerRecords<Long, UserActionAvro> records = consumer.poll(Duration.ofMillis(pollTimeout));

                    if (records.isEmpty()) {
                        continue;
                    }

                    log.info("Получено {} действий пользователей", records.count());
                    processRecords(records);

                    consumer.commitAsync();

                } catch (WakeupException e) {
                    if (running.get()) {
                        log.warn("Получен WakeupException, но сервис еще работает");
                        continue;
                    }
                    break;
                } catch (Exception e) {
                    log.error("Ошибка при опросе Kafka топика {}", topicUserAction, e);
                    sleep(5000);
                }
            }

        } catch (Exception e) {
            log.error("Критическая ошибка в UserActionService", e);
            throw e;
        } finally {
            shutdown();
        }
    }

    private void processRecords(ConsumerRecords<Long, UserActionAvro> records) {
        int processedCount = 0;
        int errorCount = 0;

        for (ConsumerRecord<Long, UserActionAvro> record : records) {
            try {
                UserActionAvro userAction = record.value();
                log.debug("Обработка действия пользователя: userId={}, eventId={}, actionType={}, offset={}",
                        userAction.getUserId(), userAction.getEventId(), userAction.getActionType(), record.offset());

                userActionHandler.handle(userAction);
                processedCount++;

            } catch (Exception e) {
                errorCount++;
                log.error("Ошибка обработки действия пользователя: userId={}, eventId={}, offset={}",
                        record.value().getUserId(), record.value().getEventId(), record.offset(), e);
            }
        }

        if (errorCount > 0) {
            log.warn("Обработка действий завершена: успешно={}, с ошибками={}", processedCount, errorCount);
        } else {
            log.info("Обработка действий завершена: успешно={}", processedCount);
        }
    }

    public void shutdown() {
        if (running.compareAndSet(true, false)) {
            log.info("Запущен graceful shutdown UserActionService...");

            try {
                consumer.wakeup();
            } catch (Exception e) {
                log.warn("Ошибка при wakeup consumer", e);
            }

            try {
                consumer.commitSync();
                log.info("Смещения действий пользователей закоммичены");
            } catch (Exception e) {
                log.error("Ошибка при коммите смещений действий", e);
            }

            try {
                consumer.close();
                log.info("Consumer действий пользователей закрыт");
            } catch (Exception e) {
                log.error("Ошибка при закрытии consumer действий", e);
            }

            log.info("UserActionService остановлен");
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
