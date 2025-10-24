package ru.practicum.request.service.service;

import ru.practicum.interaction.api.dto.request.ParticipationRequestDto;
import ru.practicum.interaction.api.dto.request.PrivateRequestParam;
import ru.practicum.request.service.model.Request;

import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> getRequestOfCurrentUser(PrivateRequestParam param);

    ParticipationRequestDto createRequest(PrivateRequestParam param);

    ParticipationRequestDto updateRequest(PrivateRequestParam param);

    List<ParticipationRequestDto> findUserEventRequests(Long userId, Long eventId);

    ParticipationRequestDto getRequest(Long requestId);

    void saveRequest(ParticipationRequestDto request);
}
