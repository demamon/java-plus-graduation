package ru.practicum.ewm.request.service;

import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.PrivateRequestParam;

import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> getRequestOfCurrentUser(PrivateRequestParam param);

    ParticipationRequestDto createRequest(PrivateRequestParam param);

    ParticipationRequestDto updateRequest(PrivateRequestParam param);
}
