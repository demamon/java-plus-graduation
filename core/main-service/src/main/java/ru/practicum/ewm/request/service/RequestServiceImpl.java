package ru.practicum.ewm.request.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.model.QRequest;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.RequestMapper;
import ru.practicum.ewm.request.RequestRepository;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.PrivateRequestParam;
import ru.practicum.ewm.request.model.RequestState;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public List<ParticipationRequestDto> getRequestOfCurrentUser(PrivateRequestParam param) {
        QRequest qRequest = QRequest.request;
        List<BooleanExpression> conditions = new ArrayList<>();

        conditions.add(QRequest.request.requester.id.eq(param.getUserId()));

        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .get();

        return RequestMapper.mapToRequestDto(requestRepository.findAll(finalCondition));
    }

    @Transactional
    @Override
    public ParticipationRequestDto createRequest(PrivateRequestParam param) {
        long userId = param.getUserId();
        User requester = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь id = %d не найден", userId))
        );
        long eventId = param.getEventId();
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(String.format("Событие id = %d не найдено", eventId))
        );

        if (checkDuplicatedRequest(param))
            throw new ConflictException("Нельзя добавить повторный запрос");
        if (event.getInitiator().getId().equals(requester.getId()))
            throw new ConflictException("Инициатор события не может добавить запрос на участие в своём событии");
        if (!event.getState().equals(EventState.PUBLISHED))
            throw new ConflictException("Нельзя учавствовать в неопубликованном событии");
        if (event.getConfirmedRequests() >= event.getParticipantLimit()
        && event.getParticipantLimit() != 0)
            throw new ConflictException("Достигнут лимит подтверждённых запросов на участие в событии");

        Request request = new Request(event, requester);

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setState(RequestState.CONFIRMED);
            event.increaseCountOfConfirmedRequest();
            eventRepository.save(event);
        } else {
            request.setState(RequestState.PENDING);
        }

        Request newRequest = requestRepository.save(request);
        return RequestMapper.mapToRequestDto(newRequest);
    }

    private boolean checkDuplicatedRequest(PrivateRequestParam param) {
        QRequest qRequest = QRequest.request;
        List<BooleanExpression> conditions = new ArrayList<>();

        conditions.add(QRequest.request.requester.id.eq(param.getUserId()));
        conditions.add(QRequest.request.event.id.eq(param.getEventId()));

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
        if (userRepository.findById(userId).isEmpty())
            throw new NotFoundException(String.format("Пользователь id = %d не найден", userId));

        long requestId = param.getRequestId();
        Request oldRequest = requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException(String.format("Запрос id = %d не найден", requestId))
        );
        Event event = oldRequest.getEvent();

        oldRequest.setState(RequestState.CANCELED);
        event.decreaseCountOfConfirmedRequest();

        Request updatedRequest = requestRepository.save(oldRequest);
        return RequestMapper.mapToRequestDto(updatedRequest);
    }

}
