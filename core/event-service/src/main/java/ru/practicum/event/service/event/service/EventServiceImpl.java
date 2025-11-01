package ru.practicum.event.service.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.service.category.model.Category;
import ru.practicum.event.service.category.repository.CategoryRepository;
import ru.practicum.event.service.event.mapper.EventMapper;
import ru.practicum.event.service.event.model.Event;
import ru.practicum.ewm.grpc.stats.event.ActionTypeProto;
import ru.practicum.ewm.grpc.stats.event.RecommendedEventProto;
import ru.practicum.interaction.api.enums.event.EventState;
import ru.practicum.event.service.event.model.Location;
import ru.practicum.event.service.event.model.QEvent;
import ru.practicum.interaction.api.dto.event.PrivateEventParam;
import ru.practicum.event.service.event.repository.EventRepository;
import ru.practicum.event.service.event.repository.LocationRepository;
import ru.practicum.event.service.exception.ConflictException;
import ru.practicum.event.service.exception.NotFoundException;
import ru.practicum.event.service.exception.ValidationException;
import ru.practicum.event.service.exception.WrongTimeEventException;
import ru.practicum.interaction.api.dto.event.*;
import ru.practicum.interaction.api.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.interaction.api.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.interaction.api.dto.request.ParticipationRequestDto;
import ru.practicum.interaction.api.enums.request.RequestState;
import ru.practicum.interaction.api.feign.request.RequestClient;
import ru.practicum.interaction.api.feign.user.UserClient;
import ru.practicum.stats.client.RecommendationsClient;
import ru.practicum.stats.client.UserActionClient;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserClient userClient;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestClient requestClient;
    private final UserActionClient userActionClient;
    private final RecommendationsClient recommendationsClient;
    private final JPAQueryFactory queryFactory;
    private final EventMapper eventMapper;

    private static String datePattern = "yyyy-MM-dd HH:mm:ss";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);

    @Override
    public List<EventFullDto> getEventsOfUser(PrivateEventParam param) {
        QEvent qEvent = QEvent.event;
        List<BooleanExpression> conditions = new ArrayList<>();

        conditions.add(QEvent.event.initiatorId.eq(param.getUserId()));

        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .get();

        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        Pageable page = PageRequest.of(param.getFrom(), param.getSize(), sortById);

        return eventMapper.mapToEventFullDtoList(eventRepository.findAll(finalCondition, page));
    }

    @Override
    public EventFullDto getEventOfUser(PrivateEventParam param) {
        return eventMapper.mapToEventFullDto(
                eventRepository.findByIdAndInitiatorId(param.getEventId(), param.getUserId()).orElseThrow(
                        () -> new NotFoundException(String.format("Событие id = %d не найдено", param.getEventId()))
                )
        );
    }

    @Transactional
    @Override
    public EventFullDto createEvent(PrivateEventParam param) {
        log.debug("получили параметры для создания события {}", param);
        NewEventDto eventFromRequest = param.getNewEvent();
        Long initiatorId = userClient.getUserFull(param.getUserId()).getId();
        Long categoryId = eventFromRequest.getCategory();
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new NotFoundException(String.format("Категория id = %d не найдена", categoryId))
        );
        Location location = locationRepository.save(eventMapper.mapFromRequest(eventFromRequest.getLocation()));
        checkEventTime(eventFromRequest.getEventDate());
        Event newEvent = eventRepository.save(eventMapper.mapFromRequest(eventFromRequest));
        newEvent.setCategory(category);
        newEvent.setInitiatorId(initiatorId);
        newEvent.setLocation(location);
        log.debug("имеем новое событие перед маппером {}", newEvent);
        log.debug("выгружаем все события из базы {}", eventRepository.findAll());
        return eventMapper.mapToEventFullDto(newEvent);
    }

    @Transactional
    @Override
    public EventFullDto updateEvent(PrivateEventParam param) {
        Event oldEvent = eventRepository.findByIdAndInitiatorId(param.getEventId(), param.getUserId()).orElseThrow(
                () -> new NotFoundException(String.format("Событие id = %d не найдено", param.getEventId()))
        );
        if (oldEvent.getState().toString().equalsIgnoreCase("PUBLISHED"))
            throw new ConflictException("Событие в публикации не может быть изменено");
        UpdateEventUserRequest eventFromRequest = param.getEventOnUpdate();
        if (eventFromRequest.getEventDate() != null)
            checkEventTime(eventFromRequest.getEventDate());
        Event newEvent = eventMapper.updatePrivateEventFields(oldEvent, eventFromRequest);
        eventRepository.save(newEvent);
        return eventMapper.mapToEventFullDto(newEvent);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsOfUser(PrivateEventParam param) {
        log.debug("возвращаем запросы на события пользователя {}", param);
        eventRepository.findByIdAndInitiatorId(param.getEventId(), param.getUserId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Событие id = %d не найдено у пользователя id = %d",
                                param.getEventId(), param.getUserId()))
                );
        return requestClient.findUserEventRequests(
                param.getUserId(),
                param.getEventId()
        );
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateStatusOfRequests(PrivateEventParam param) {
        long userId = param.getUserId();
        userClient.getUserFull(userId);
        long eventId = param.getEventId();
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(
                () -> new NotFoundException(String.format("Событие id = %d не найдено", eventId))
        );
        EventRequestStatusUpdateRequest requestOnUpdateStatus = param.getRequest();
        EventRequestStatusUpdateResult updatedRequests = EventRequestStatusUpdateResult.builder()
                .confirmedRequests(new ArrayList<>())
                .rejectedRequests(new ArrayList<>())
                .build();

        if ((event.getParticipantLimit() == 0 || !event.getRequestModeration())
                && requestOnUpdateStatus.getStatus().equalsIgnoreCase("confirmed"))
            return updatedRequests;

        for (Long requestId : requestOnUpdateStatus.getRequestIds()) {
            ParticipationRequestDto request = requestClient.getRequest(userId, requestId);

            if (!request.getStatus().equals(RequestState.PENDING))
                throw new ConflictException("Статус можно изменить только у заявок, находящихся в ожидании");

            if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
                request.setStatus(RequestState.REJECTED);
                requestClient.saveRequest(userId, request);
            }

            if (requestOnUpdateStatus.getStatus().equalsIgnoreCase("confirmed")) {
                if (event.getConfirmedRequests() >= event.getParticipantLimit())
                    throw new ConflictException("Достигнут лимит подтверждённых запросов на участие в событии");
                request.setStatus(RequestState.CONFIRMED);
                requestClient.saveRequest(userId, request);
                event.increaseCountOfConfirmedRequest();
                eventRepository.save(event);
                updatedRequests.addConfirmedRequest(request);
            } else if (requestOnUpdateStatus.getStatus().equalsIgnoreCase("rejected")) {
                request.setStatus(RequestState.REJECTED);
                requestClient.saveRequest(userId, request);
                updatedRequests.addRejectedRequest(request);
            } else
                throw new ValidationException("Заявки можно только подтверждать или отклонять");
        }
        return updatedRequests;
    }

    @Override
    public Collection<EventShortDto> getPublicAllEvents(EventFilter filter, HttpServletRequest request) {
        log.debug("параметры для фильтрации {}", filter);
        log.debug("все события что есть в бд {}", eventRepository.findAll());
        checkFilterDateRangeIsGood(filter.getRangeStart(), filter.getRangeEnd());
        List<Event> events;
        QEvent event = QEvent.event;
        BooleanExpression exp;
        exp = event.state.eq(EventState.PUBLISHED);
        if (filter.getText() != null && !filter.getText().isBlank()) {
            exp = exp.and(event.description.containsIgnoreCase(filter.getText()))
                    .or(event.annotation.containsIgnoreCase(filter.getText()));
        }
        if (filter.getCategories() != null) {
            exp = exp.and(event.category.id.in(filter.getCategories()));
        }
        if (filter.getPaid() != null) {
            exp = exp.and(event.paid.eq(filter.getPaid()));
        }
        if (filter.getRangeStart() != null) {
            exp = exp.and(event.eventDate.after(filter.getRangeStart()));
        }
        if (filter.getRangeEnd() != null) {
            exp = exp.and(event.eventDate.before(filter.getRangeEnd()));
        }
        if (filter.getOnlyAvailable()) {
            exp = exp.and(event.participantLimit.gt(event.confirmedRequests));
        }
        log.debug("sql запрос к бд {}", exp);
        JPAQuery<Event> query = queryFactory.selectFrom(event)
                .where(exp)
                .offset(filter.getFrom())
                .limit(filter.getSize());
        events = query.fetch();
        log.debug("события получаемы из бд после фильтрации {}", events);
        return events.stream()
                .map(eventMapper::mapToEventShortDto)
                .sorted(Comparator.comparing(EventShortDto::getEventDate))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Collection<EventFullDto> getAdminAllEvents(EventFilter filter) {
        log.debug("параметры для фильтрации {}", filter);
        log.debug("все события что есть в бд {}", eventRepository.findAll());
        checkFilterDateRangeIsGood(filter.getRangeStart(), filter.getRangeEnd());
        List<Event> events;
        QEvent event = QEvent.event;
        BooleanExpression exp = Expressions.asBoolean(true).isTrue();
        if (filter.getStates() != null) {
            List<EventState> eventStates = filter.getStates().stream()
                    .map(EventState::valueOf)
                    .toList();
            exp = event.state.in(eventStates);
        }
        if (filter.getUsers() != null) {
            exp = exp.and(event.initiatorId.in(filter.getUsers()));
        }
        if (filter.getCategories() != null) {
            exp = exp.and(event.category.id.in(filter.getCategories()));
        }
        if (filter.getRangeStart() != null) {
            exp = exp.and(event.eventDate.after(filter.getRangeStart()));
        }
        if (filter.getRangeEnd() != null) {
            exp = exp.and(event.eventDate.before(filter.getRangeEnd()));
        }
        log.debug("sql запрос к бд {}", exp);
        JPAQuery<Event> query = queryFactory.selectFrom(event)
                .where(exp)
                .offset(filter.getFrom())
                .limit(filter.getSize());
        events = query.fetch();
        log.debug("события получаемы из бд после фильтрации {}", events);
        return events.stream()
                .map(eventMapper::mapToEventFullDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public EventFullDto getPublicEvent(Long eventId, Long userId) {
        final Event event = findEventById(eventId);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }

        final EventFullDto eventDto = eventMapper.mapToEventFullDto(event);
        userActionClient.collectUserAction(eventId, userId, ActionTypeProto.ACTION_VIEW, Instant.now());
        return eventDto;
    }

    @Transactional
    @Override
    public EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest updateEvent) {
        Event event = findEventById(eventId);
        log.debug("событие что мы нашли {}", event);
        log.debug("сам запрос на обновление {}", updateEvent);
        validateEventDateForAdmin(updateEvent.getEventDate() == null ? event.getEventDate() :
                LocalDateTime.parse(updateEvent.getEventDate(), formatter), updateEvent.getStateAction());
        validateStatusForAdmin(event.getState(), updateEvent.getStateAction());
        if (updateEvent.getLocation() != null) {
            Location newLocation = locationRepository.save(eventMapper.mapFromRequest(updateEvent.getLocation()));
            log.debug("сохранили новую локацию {}", newLocation);
            event.setLocation(newLocation);
        }
        eventMapper.updateAdminEventFields(event, updateEvent);
        if (event.getState() != null && event.getState().equals(EventState.PUBLISHED)) {
            event.setPublishedOn(LocalDateTime.now());
        }
        eventRepository.save(event);
        log.debug("обновленное событие {}", event);
        return eventMapper.mapToEventFullDto(event);
    }

    @Override
    public EventFullDto getEvent(Long eventId) {
        Optional<Event> mayBeEvent = eventRepository.findById(eventId);
        if (mayBeEvent.isEmpty()) {
            throw new NotFoundException("события с id = " + eventId + "не найдено");
        }
        Event event = mayBeEvent.get();
        return eventMapper.mapToEventFullDto(event);
    }

    @Transactional
    @Override
    public void increaseCountOfConfirmedRequest(Long eventId) {
        Event event = eventRepository.findById(eventId).get();
        log.debug("события для увеличения счетчика запросов {}", event);
        event.increaseCountOfConfirmedRequest();
        log.debug("счетчик увеличили {}", event);
        eventRepository.save(event);
    }

    @Transactional
    @Override
    public void decreaseCountOfConfirmedRequest(Long eventId) {
        Event event = eventRepository.findById(eventId).get();
        event.decreaseCountOfConfirmedRequest();
        eventRepository.save(event);
    }

    @Override
    public List<EventShortDto> getEventsRecommendations(Long userId, Integer maxResults) {
        Map<Long, Double> recommendations = recommendationsClient
                .getRecommendationsForUser(userId, maxResults)
                .collect(Collectors.toMap(RecommendedEventProto::getEventId, RecommendedEventProto::getScore));

        List<Event> events = eventRepository.findAllById(recommendations.keySet());
        return eventMapper.mapToEventShortDto(events);
    }

    @Override
    public void addLikeToEvent(Long eventId, Long userId) {
        if (!requestClient.checkExistStatusRequest(eventId, userId, RequestState.CONFIRMED)) {
            throw new ValidationException("Пользователь не участвует в этом событии.");
        }
        userActionClient.collectUserAction(eventId, userId, ActionTypeProto.ACTION_LIKE, Instant.now());
    }

    private void checkFilterDateRangeIsGood(LocalDateTime dateBegin, LocalDateTime dateEnd) {
        if (dateBegin == null) {
            return;
        }
        if (dateEnd == null) {
            return;
        }

        if (dateBegin.isAfter(dateEnd)) {
            throw new ValidationException(
                    "Неверно задана дата начала и конца события в фильтре");
        }
    }

    private Event findEventById(Long eventId) {

        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Публичное событие с id = " + eventId + " не найдено")
        );
    }

    private void validateEventDateForAdmin(LocalDateTime eventDate, String stateAction) {
        if (stateAction != null && stateAction.equals("PUBLISH_EVENT") &&
                eventDate.isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ConflictException("Прошло более часа с момента публикации события");
        }
        if (eventDate.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата события не может быть в прошлом");
        }
    }

    private void validateStatusForAdmin(EventState eventState, String stateAction) {
        if (!eventState.equals(EventState.PENDING) && stateAction.equals("PUBLISH_EVENT")) {
            throw new ConflictException("Событие не в ожидании публикации");
        }
        if (eventState.equals(EventState.PUBLISHED) && stateAction.equals("REJECT_EVENT")) {
            throw new ConflictException("Нельзя отклонить опубликованное событие");
        }
    }

    private void checkEventTime(String eventDateStr) {
        String datePattern = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);

        LocalDateTime requestEventDate = LocalDateTime.parse(eventDateStr, formatter);
        Duration duration = Duration.between(LocalDateTime.now(), requestEventDate);
        Duration minDuration = duration.minusHours(2);
        if (minDuration.isNegative() && !minDuration.isZero()) {
            throw new WrongTimeEventException(
                    "Событие должно наступить минимум через 2 часа от момента добавления события");
        }
    }
}
