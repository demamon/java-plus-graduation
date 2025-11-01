package ru.practicum.analyzer.controller;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.analyzer.handler.RecommendationsHandler;
import ru.practicum.ewm.grpc.stats.controller.RecommendationsControllerGrpc;
import  ru.practicum.ewm.grpc.stats.event.*;

import java.util.List;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class RecommendationsController extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {

    private final RecommendationsHandler handler;

    @Override
    public void getRecommendationsForUser(UserPredictionsRequestProto request,
                                          StreamObserver<RecommendedEventProto> responseObserver) {
        log.info("Получение рекомендаций для пользователя: userId={}, maxResults={}",
                request.getUserId(), request.getMaxResults());

        try {
            if (request.getUserId() <= 0) {
                throw new IllegalArgumentException("User ID должен быть положительным числом");
            }
            if (request.getMaxResults() <= 0) {
                throw new IllegalArgumentException("Max results должен быть положительным числом");
            }

            List<RecommendedEventProto> recommendations = handler.getRecommendationsForUser(request);

            log.debug("Найдено {} рекомендаций для пользователя {}",
                    recommendations.size(), request.getUserId());

            for (RecommendedEventProto recommendation : recommendations) {
                responseObserver.onNext(recommendation);
            }

            responseObserver.onCompleted();
            log.info("Рекомендации успешно отправлены для пользователя {}", request.getUserId());

        } catch (IllegalArgumentException e) {
            log.warn("Некорректный запрос рекомендаций: userId={}, error={}",
                    request.getUserId(), e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        } catch (Exception e) {
            log.error("Ошибка получения рекомендаций для пользователя {}: {}",
                    request.getUserId(), e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Внутренняя ошибка сервера при получении рекомендаций")
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    @Override
    public void getSimilarEvents(SimilarEventsRequestProto request,
                                 StreamObserver<RecommendedEventProto> responseObserver) {
        log.info("Поиск похожих мероприятий: eventId={}, userId={}, maxResults={}",
                request.getEventId(), request.getUserId(), request.getMaxResults());

        try {
            if (request.getEventId() <= 0) {
                throw new IllegalArgumentException("Event ID должен быть положительным числом");
            }
            if (request.getUserId() <= 0) {
                throw new IllegalArgumentException("User ID должен быть положительным числом");
            }

            List<RecommendedEventProto> similarEvents = handler.getSimilarEvents(request);

            log.debug("Найдено {} похожих мероприятий для eventId {}",
                    similarEvents.size(), request.getEventId());

            for (RecommendedEventProto event : similarEvents) {
                responseObserver.onNext(event);
            }

            responseObserver.onCompleted();
            log.info("Похожие мероприятия успешно отправлены для eventId {}", request.getEventId());

        } catch (IllegalArgumentException e) {
            log.warn("Некорректный запрос похожих мероприятий: eventId={}, error={}",
                    request.getEventId(), e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        } catch (Exception e) {
            log.error("Ошибка поиска похожих мероприятий для eventId {}: {}",
                    request.getEventId(), e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Внутренняя ошибка сервера при поиске похожих мероприятий")
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    @Override
    public void getInteractionsCount(InteractionsCountRequestProto request,
                                     StreamObserver<RecommendedEventProto> responseObserver) {
        log.info("Получение количества взаимодействий для {} мероприятий",
                request.getEventIdCount());

        try {
            if (request.getEventIdCount() == 0) {
                throw new IllegalArgumentException("Список event IDs не может быть пустым");
            }
            for (Long eventId : request.getEventIdList()) {
                if (eventId <= 0) {
                    throw new IllegalArgumentException("Event ID должен быть положительным числом: " + eventId);
                }
            }

            List<RecommendedEventProto> interactions = handler.getInteractionsCount(request);

            log.debug("Получено {} записей о взаимодействиях", interactions.size());

            for (RecommendedEventProto interaction : interactions) {
                responseObserver.onNext(interaction);
            }

            responseObserver.onCompleted();
            log.info("Данные о взаимодействиях успешно отправлены");

        } catch (IllegalArgumentException e) {
            log.warn("Некорректный запрос взаимодействий: error={}", e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        } catch (Exception e) {
            log.error("Ошибка получения количества взаимодействий: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Внутренняя ошибка сервера при получении взаимодействий")
                    .withCause(e)
                    .asRuntimeException());
        }
    }
}
