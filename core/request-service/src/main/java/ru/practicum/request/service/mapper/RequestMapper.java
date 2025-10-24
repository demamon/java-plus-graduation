package ru.practicum.request.service.mapper;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.interaction.api.dto.request.ParticipationRequestDto;
import ru.practicum.request.service.model.Request;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RequestMapper {
    private static String datePattern = "yyyy-MM-dd HH:mm:ss";
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);

    public static ParticipationRequestDto mapToRequestDto(Request request) {
        log.debug("передаем запрос в маппер {}", request);
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .event(request.getEventId())
                .requester(request.getRequesterId())
                .status(request.getState())
                .created(request.getCreated().format(formatter))
                .build();
    }

    public static List<ParticipationRequestDto> mapToRequestDto(Iterable<Request> requests) {
        List<ParticipationRequestDto> requestsResult = new ArrayList<>();
        for (Request request : requests) {
            log.debug("запрос для обработки {}", request);
            requestsResult.add(mapToRequestDto(request));
        }

        return requestsResult;
    }

    public static Request mapToRequest(ParticipationRequestDto dto) {
        Request request = new Request();
        request.setId(dto.getId());
        request.setRequesterId(dto.getRequester());
        request.setEventId(dto.getEvent());
        request.setState(dto.getStatus());
        request.setCreated(LocalDateTime.parse(dto.getCreated(), formatter));
        return request;
    }

}