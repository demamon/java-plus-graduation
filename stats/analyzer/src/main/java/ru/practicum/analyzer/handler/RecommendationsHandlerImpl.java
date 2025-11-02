package ru.practicum.analyzer.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.analyzer.model.EventSimilarity;
import ru.practicum.analyzer.model.UserAction;
import ru.practicum.analyzer.repository.EventSimilarityRepository;
import ru.practicum.analyzer.repository.UserActionRepository;
import ru.practicum.ewm.grpc.stats.event.InteractionsCountRequestProto;
import ru.practicum.ewm.grpc.stats.event.RecommendedEventProto;
import ru.practicum.ewm.grpc.stats.event.SimilarEventsRequestProto;
import ru.practicum.ewm.grpc.stats.event.UserPredictionsRequestProto;


import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationsHandlerImpl implements RecommendationsHandler {

    private final UserActionRepository userActionRepository;
    private final EventSimilarityRepository eventSimilarityRepository;

    @Override
    public List<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request) {
        Long userId = request.getUserId();
        int maxResults = request.getMaxResults();

        log.debug("Получение рекомендаций для пользователя: userId={}, maxResults={}", userId, maxResults);

        List<UserAction> userActions = userActionRepository.findAllByUserId(userId,
                PageRequest.of(0, maxResults, Sort.by(Sort.Direction.DESC, "timestamp")));

        if (userActions.isEmpty()) {
            log.debug("Пользователь {} не имеет действий, возвращаем пустой список", userId);
            return List.of();
        }

        Set<Long> userEventIds = userActions.stream()
                .map(UserAction::getEventId)
                .collect(Collectors.toSet());

        log.debug("Пользователь {} взаимодействовал с {} событиями", userId, userEventIds.size());

        Set<Long> recommendedEventIds = findSimilarEvents(userEventIds, maxResults * 2); // Берем с запасом

        recommendedEventIds.removeAll(userEventIds);

        log.debug("Найдено {} потенциальных рекомендаций", recommendedEventIds.size());

        return recommendedEventIds.stream()
                .map(eventId -> RecommendedEventProto.newBuilder()
                        .setEventId(eventId)
                        .setScore(calculatePredictedScore(eventId, userId, userEventIds, maxResults))
                        .build())
                .sorted(Comparator.comparing(RecommendedEventProto::getScore).reversed())
                .limit(maxResults)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request) {
        Long eventId = request.getEventId();
        Long userId = request.getUserId();
        int maxResults = request.getMaxResults();

        log.debug("Поиск похожих событий: eventId={}, userId={}, maxResults={}", eventId, userId, maxResults);

        Set<EventSimilarity> similarities = new HashSet<>();
        similarities.addAll(eventSimilarityRepository.findAllByEventA(eventId,
                PageRequest.of(0, maxResults * 2, Sort.by(Sort.Direction.DESC, "score"))));
        similarities.addAll(eventSimilarityRepository.findAllByEventB(eventId,
                PageRequest.of(0, maxResults * 2, Sort.by(Sort.Direction.DESC, "score"))));

        return similarities.stream()
                .map(es -> {
                    Long similarEventId = es.getEventA().equals(eventId) ? es.getEventB() : es.getEventA();
                    return Map.entry(similarEventId, es.getScore());
                })
                .filter(entry -> !userActionRepository.existsByEventIdAndUserId(entry.getKey(), userId))
                .map(entry -> RecommendedEventProto.newBuilder()
                        .setEventId(entry.getKey())
                        .setScore(entry.getValue())
                        .build())
                .sorted(Comparator.comparing(RecommendedEventProto::getScore).reversed())
                .limit(maxResults)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request) {
        log.debug("Получение количества взаимодействий для {} событий", request.getEventIdCount());

        return request.getEventIdList().stream()
                .map(eventId -> {
                    Float sumWeight = userActionRepository.getSumWeightByEventId(eventId);
                    return RecommendedEventProto.newBuilder()
                            .setEventId(eventId)
                            .setScore(sumWeight != null ? sumWeight : 0.0f)
                            .build();
                })
                .sorted(Comparator.comparing(RecommendedEventProto::getScore).reversed())
                .collect(Collectors.toList());
    }

    private float calculatePredictedScore(Long targetEventId, Long userId, Set<Long> userEventIds, int k) {
        List<EventSimilarity> nearestNeighbors = findKNearestNeighbors(targetEventId, userEventIds, k);

        if (nearestNeighbors.isEmpty()) {
            log.debug("Для события {} не найдено похожих событий с взаимодействиями пользователя", targetEventId);
            return 0.0f;
        }

        Map<Long, Float> userRatings = userActionRepository.findAllByEventIdInAndUserId(
                nearestNeighbors.stream()
                        .map(es -> es.getEventA().equals(targetEventId) ? es.getEventB() : es.getEventA())
                        .collect(Collectors.toSet()),
                userId
        ).stream().collect(Collectors.toMap(UserAction::getEventId, UserAction::getMark));

        double weightedSum = 0.0;
        double similaritySum = 0.0;

        for (EventSimilarity neighbor : nearestNeighbors) {
            Long neighborEventId = neighbor.getEventA().equals(targetEventId) ? neighbor.getEventB() : neighbor.getEventA();
            Float userRating = userRatings.get(neighborEventId);

            if (userRating != null) {
                weightedSum += neighbor.getScore() * userRating;
                similaritySum += neighbor.getScore();
            }
        }

        if (similaritySum == 0) {
            return 0.0f;
        }

        float predictedScore = (float) (weightedSum / similaritySum);
        log.debug("Предсказанная оценка для события {}: {}", targetEventId, predictedScore);

        return predictedScore;
    }

    private Set<Long> findSimilarEvents(Set<Long> sourceEventIds, int limit) {
        Set<Long> similarEvents = new HashSet<>();

        similarEvents.addAll(
                eventSimilarityRepository.findAllByEventAIn(sourceEventIds,
                                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "score")))
                        .stream()
                        .map(EventSimilarity::getEventB)
                        .collect(Collectors.toSet())
        );

        similarEvents.addAll(
                eventSimilarityRepository.findAllByEventBIn(sourceEventIds,
                                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "score")))
                        .stream()
                        .map(EventSimilarity::getEventA)
                        .collect(Collectors.toSet())
        );

        return similarEvents;
    }

    private List<EventSimilarity> findKNearestNeighbors(Long targetEventId, Set<Long> userEventIds, int k) {
        Set<EventSimilarity> allSimilarities = new HashSet<>();

        allSimilarities.addAll(eventSimilarityRepository.findAllByEventAAndEventBIn(
                targetEventId, userEventIds,
                PageRequest.of(0, k, Sort.by(Sort.Direction.DESC, "score"))
        ));

        allSimilarities.addAll(eventSimilarityRepository.findAllByEventBAndEventAIn(
                targetEventId, userEventIds,
                PageRequest.of(0, k, Sort.by(Sort.Direction.DESC, "score"))
        ));

        return allSimilarities.stream()
                .sorted(Comparator.comparing(EventSimilarity::getScore).reversed())
                .limit(k)
                .collect(Collectors.toList());
    }
}
