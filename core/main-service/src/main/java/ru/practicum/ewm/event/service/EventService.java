package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.comment.CommentDto;
import ru.practicum.ewm.event.param.AdminCommentParam;
import ru.practicum.ewm.event.param.OpenCommentParam;
import ru.practicum.ewm.event.param.PrivateCommentParam;
import ru.practicum.ewm.event.param.PrivateEventParam;
import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.ewm.event.dto.*;

import java.util.List;

import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.Collection;

public interface EventService {

    List<EventFullDto> getEventsOfUser(PrivateEventParam param);

    EventFullDto getEventOfUser(PrivateEventParam param);

    EventFullDto createEvent(PrivateEventParam param);

    EventFullDto updateEvent(PrivateEventParam param);

    List<ParticipationRequestDto> getRequestsOfUser(PrivateEventParam param);

    EventRequestStatusUpdateResult updateStatusOfRequests(PrivateEventParam param);

    Collection<EventShortDto> getPublicAllEvents(EventFilter filter, HttpServletRequest request);

    EventFullDto getPublicEvent(Long eventId, HttpServletRequest request);

    Collection<EventFullDto> getAdminAllEvents(EventFilter filter);

    EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest updateEvent);

    List<CommentDto> getCommentsOfUser(PrivateCommentParam param);

    CommentDto createComment(PrivateCommentParam param);

    List<CommentDto> getComments(OpenCommentParam param);

    CommentDto getCommentById(AdminCommentParam param);

    CommentDto updateComment(AdminCommentParam param);

    void removeComment(AdminCommentParam param);
}
