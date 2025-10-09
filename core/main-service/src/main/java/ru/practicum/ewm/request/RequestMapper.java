package ru.practicum.ewm.request;

import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.model.Request;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RequestMapper {
    private static String datePattern = "yyyy-MM-dd HH:mm:ss";
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);

    public static ParticipationRequestDto mapToRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getState().name())
                .created(request.getCreated().format(formatter))
                .build();
    }

    public static List<ParticipationRequestDto> mapToRequestDto(Iterable<Request> requests) {
        List<ParticipationRequestDto> requestsResult = new ArrayList<>();

        for (Request request : requests) {
            requestsResult.add(mapToRequestDto(request));
        }

        return requestsResult;
    }

}