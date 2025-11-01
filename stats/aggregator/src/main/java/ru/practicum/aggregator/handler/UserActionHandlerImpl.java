package ru.practicum.aggregator.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserActionHandlerImpl implements UserActionHandler {

    // Хранилище действий пользователей: eventId -> (userId -> maxWeight)
    private final Map<Long, Map<Long, Double>> eventUserWeights = new ConcurrentHashMap<>();

    // Суммарные веса по событиям: eventId -> totalWeight
    private final Map<Long, Double> eventTotalWeights = new ConcurrentHashMap<>();

    // Кэш сумм минимальных весов: min(eventA, eventB) -> (max(eventA, eventB) -> minWeightsSum)
    private final Map<Long, Map<Long, Double>> minWeightsCache = new ConcurrentHashMap<>();


    @Value("${application.action-weight.view}")
    private float viewWeight;
    @Value("${application.action-weight.register}")
    private float registerWeight;
    @Value("${application.action-weight.like}")
    private float likeWeight;

    @Override
    public List<EventSimilarityAvro> calculateSimilarity(UserActionAvro userAction) {
        Long userId = userAction.getUserId();
        Long eventId = userAction.getEventId();

        log.debug("Обработка действия пользователя: userId={}, eventId={}, type={}",
                userId, eventId, userAction.getActionType());

        Optional<WeightUpdate> weightUpdate = processUserAction(userAction);
        if (weightUpdate.isEmpty()) {
            log.debug("Вес не изменился, расчет схожести не требуется");
            return Collections.emptyList();
        }

        return calculateSimilarityScores(eventId, userId, weightUpdate.get());
    }

    /**
     * Обрабатывает действие пользователя и возвращает информацию об изменении веса
     */
    private Optional<WeightUpdate> processUserAction(UserActionAvro userAction) {
        Long eventId = userAction.getEventId();
        Long userId = userAction.getUserId();
        double newWeight = getActionWeight(userAction.getActionType());

        Map<Long, Double> userWeights = eventUserWeights.computeIfAbsent(eventId, k -> new ConcurrentHashMap<>());
        Double oldWeight = userWeights.get(userId);

        // Вес обновляется только если он увеличился
        if (oldWeight != null && newWeight <= oldWeight) {
            log.debug("Вес не увеличился: eventId={}, userId={}, current={}, new={}",
                    eventId, userId, oldWeight, newWeight);
            return Optional.empty();
        }

        // Вычисляем разницу для обновления суммарного веса
        double weightDiff = (oldWeight == null) ? newWeight : newWeight - oldWeight;
        userWeights.put(userId, newWeight);
        eventTotalWeights.merge(eventId, weightDiff, Double::sum);

        log.debug("Обновлен вес: eventId={}, userId={}, oldWeight={}, newWeight={}, diff={}",
                eventId, userId, oldWeight, newWeight, weightDiff);

        return Optional.of(new WeightUpdate(weightDiff, oldWeight != null ? oldWeight : 0.0, newWeight));
    }

    private List<EventSimilarityAvro> calculateSimilarityScores(Long currentEventId, Long userId, WeightUpdate weightUpdate) {
        Set<Long> relevantEvents = findRelevantEvents(currentEventId, userId);
        log.debug("Расчет схожести с {} релевантными событиями", relevantEvents.size());

        List<EventSimilarityAvro> similarities = new ArrayList<>();
        for (Long otherEventId : relevantEvents) {
            Optional<EventSimilarityAvro> similarity = calculateEventSimilarity(
                    currentEventId, otherEventId, userId, weightUpdate);
            similarity.ifPresent(similarities::add);
        }

        return similarities;
    }

    private Set<Long> findRelevantEvents(Long currentEventId, Long userId) {
        return eventUserWeights.keySet().stream()
                .filter(eventId -> !eventId.equals(currentEventId))
                .filter(eventId -> hasUserInteracted(eventId, userId))
                .collect(Collectors.toSet());
    }

    private boolean hasUserInteracted(Long eventId, Long userId) {
        Map<Long, Double> userWeights = eventUserWeights.get(eventId);
        return userWeights != null && userWeights.containsKey(userId);
    }

    private Optional<EventSimilarityAvro> calculateEventSimilarity(Long eventA, Long eventB,
                                                                   Long userId, WeightUpdate weightUpdate) {
        double minWeightsSum = getMinWeightsSum(eventA, eventB, weightUpdate.diff(), userId);
        double similarityScore = computeCosineSimilarity(eventA, eventB, minWeightsSum);

        if (similarityScore <= 0.0) {
            return Optional.empty();
        }

        EventSimilarityAvro similarityEvent = buildSimilarityEvent(eventA, eventB, similarityScore);
        log.debug("Рассчитана схожесть: events=({}, {}), score={}", eventA, eventB, similarityScore);

        return Optional.of(similarityEvent);
    }

    private Double getMinWeightsSum(Long eventA, Long eventB, Double diff, Long userId) {
        Long firstEvent = Math.min(eventA, eventB);
        Long secondEvent = Math.max(eventA, eventB);

        Double weightA = eventUserWeights.get(eventA).get(userId);
        Double weightB = eventUserWeights.get(eventB).get(userId);

        if (weightB == null) {
            return 0.0;
        }

        Map<Long, Double> cache = minWeightsCache.computeIfAbsent(firstEvent, k -> new ConcurrentHashMap<>());
        Double currentSum = cache.get(secondEvent);

        // Если суммы еще нет, вычисляем полную сумму
        if (currentSum == null) {
            currentSum = calculateInitialMinWeightsSum(eventA, eventB);
            cache.put(secondEvent, currentSum);
            return currentSum;
        }

        // Инкрементальное обновление
        double newWeight;
        if (weightA > weightB && (weightA - diff) < weightB) {
            newWeight = currentSum + (weightB - (weightA - diff));
        } else if (weightA <= weightB) {
            newWeight = currentSum + diff;
        } else {
            return currentSum;
        }

        cache.put(secondEvent, newWeight);
        return newWeight;
    }

    private Double calculateInitialMinWeightsSum(Long eventA, Long eventB) {
        List<Double> weights = new ArrayList<>();
        Map<Long, Double> userActionsA = eventUserWeights.get(eventA);
        Map<Long, Double> userActionsB = eventUserWeights.get(eventB);

        userActionsA.forEach((aUser, aWeight) -> {
            if (userActionsB.containsKey(aUser)) {
                weights.add(Math.min(aWeight, userActionsB.get(aUser)));
            }
        });

        if (weights.isEmpty()) {
            return 0.0;
        }

        return weights.stream().mapToDouble(Double::doubleValue).sum();
    }

    private double computeCosineSimilarity(Long eventA, Long eventB, double minWeightsSum) {
        Double totalWeightA = eventTotalWeights.get(eventA);
        Double totalWeightB = eventTotalWeights.get(eventB);

        if (totalWeightA == null || totalWeightB == null || totalWeightA <= 0 || totalWeightB <= 0) {
            return 0.0;
        }

        double denominator = Math.sqrt(totalWeightA) * Math.sqrt(totalWeightB);
        if (denominator == 0.0) {
            return 0.0;
        }

        return minWeightsSum / denominator;
    }

    private double getActionWeight(ActionTypeAvro actionType) {
        return switch (actionType) {
            case VIEW -> viewWeight;
            case REGISTER -> registerWeight;
            case LIKE -> likeWeight;
        };
    }

    private EventSimilarityAvro buildSimilarityEvent(Long eventA, Long eventB, double similarity) {
        return EventSimilarityAvro.newBuilder()
                .setEventA(Math.min(eventA, eventB))
                .setEventB(Math.max(eventA, eventB))
                .setScore(similarity)
                .setTimestamp(Instant.now())
                .build();
    }

    private record WeightUpdate(double diff, double oldWeight, double newWeight) {
    }
}

