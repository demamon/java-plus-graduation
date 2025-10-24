package ru.practicum.request.service.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.interaction.api.dto.event.EventFullDto;
import ru.practicum.interaction.api.dto.user.UserDto;
import ru.practicum.interaction.api.enums.event.EventState;
import ru.practicum.interaction.api.feign.event.EventClient;
import ru.practicum.request.service.exception.ConflictException;
import ru.practicum.request.service.exception.NotFoundException;
import ru.practicum.request.service.model.Request;
import ru.practicum.request.service.model.QRequest;
import ru.practicum.request.service.mapper.RequestMapper;
import ru.practicum.request.service.repository.RequestRepository;
import ru.practicum.interaction.api.dto.request.ParticipationRequestDto;
import ru.practicum.interaction.api.dto.request.PrivateRequestParam;
import ru.practicum.interaction.api.enums.request.RequestState;
import ru.practicum.interaction.api.feign.user.UserClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventClient eventClient;
    private final UserClient userClient;

    @Override
    public List<ParticipationRequestDto> getRequestOfCurrentUser(PrivateRequestParam param) {
        QRequest qRequest = QRequest.request;
        List<BooleanExpression> conditions = new ArrayList<>();

        conditions.add(QRequest.request.requesterId.eq(param.getUserId()));

        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .get();

        return RequestMapper.mapToRequestDto(requestRepository.findAll(finalCondition));
    }

    @Transactional
    @Override
    public ParticipationRequestDto createRequest(PrivateRequestParam param) {
        long userId = param.getUserId();
        UserDto requester = userClient.getUserFull(userId);
        long eventId = param.getEventId();
        EventFullDto event = eventClient.getEvent(eventId);
        log.debug("юзер подающий запрос {}, событие {}", requester, event);
        if (checkDuplicatedRequest(param))
            throw new ConflictException("Нельзя добавить повторный запрос");
        if (event.getInitiator().getId().equals(requester.getId()))
            throw new ConflictException("Инициатор события не может добавить запрос на участие в своём событии");
        if (!event.getState().equals(EventState.PUBLISHED))
            throw new ConflictException("Нельзя участвовать в неопубликованном событии");
        if (event.getConfirmedRequests() >= event.getParticipantLimit()
                && event.getParticipantLimit() != 0)
            throw new ConflictException("Достигнут лимит подтверждённых запросов на участие в событии");

        Request request = new Request(event.getId(), requester.getId());

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setState(RequestState.CONFIRMED);
            log.debug("увеличиваем количество запросов на 1");
            eventClient.increaseCountOfConfirmedRequest(event.getId());
        } else {
            request.setState(RequestState.PENDING);
        }

        Request newRequest = requestRepository.save(request);
        return RequestMapper.mapToRequestDto(newRequest);
    }

    private boolean checkDuplicatedRequest(PrivateRequestParam param) {
        QRequest qRequest = QRequest.request;
        List<BooleanExpression> conditions = new ArrayList<>();

        conditions.add(QRequest.request.requesterId.eq(param.getUserId()));
        conditions.add(QRequest.request.eventId.eq(param.getEventId()));

        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .get();

        Iterable<Request> requestsFromRep = requestRepository.findAll(finalCondition);
        List<ParticipationRequestDto> requestsDto = RequestMapper.mapToRequestDto(requestsFromRep);

        return !requestsDto.isEmpty();
    }

    @Transactional
    @Override
    public ParticipationRequestDto updateRequest(PrivateRequestParam param) {
        long userId = param.getUserId();
        userClient.getUserFull(userId);
        long requestId = param.getRequestId();
        Request oldRequest = requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException(String.format("Запрос id = %d не найден", requestId))
        );
        EventFullDto event = eventClient.getEvent(oldRequest.getEventId());

        oldRequest.setState(RequestState.CANCELED);
        eventClient.decreaseCountOfConfirmedRequest(event.getId());

        Request updatedRequest = requestRepository.save(oldRequest);
        return RequestMapper.mapToRequestDto(updatedRequest);
    }

    @Override
    public List<ParticipationRequestDto> findUserEventRequests(Long userId, Long eventId) {
        log.debug("Поиск запросов: userId = {}, eventId = {}", userId, eventId);

        List<Request> allRequests = requestRepository.findAll();

        log.debug("Все запросы в БД: {}", allRequests);

        BooleanExpression condition = QRequest.request.eventId.eq(eventId);

        Iterable<Request> requestsIterable = requestRepository.findAll(condition);

        List<Request> debugList = new ArrayList<>();
        requestsIterable.forEach(debugList::add);
        log.debug("Найденные запросы: {}", debugList);

        return RequestMapper.mapToRequestDto(requestsIterable);
    }

    @Override
    public ParticipationRequestDto getRequest(Long requestId) {
        Optional<Request> mayBeRequest = requestRepository.findById(requestId);
        if (mayBeRequest.isEmpty()) {
            throw new NotFoundException("запрос с id = " + requestId + "не найден");
        }
        return RequestMapper.mapToRequestDto(mayBeRequest.get());
    }

    @Transactional
    @Override
    public void saveRequest(ParticipationRequestDto requestDto) {
        Request request = RequestMapper.mapToRequest(requestDto);
        requestRepository.save(request);
    }

}
