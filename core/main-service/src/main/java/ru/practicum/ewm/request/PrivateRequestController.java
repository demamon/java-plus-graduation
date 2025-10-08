package ru.practicum.ewm.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{user-id}/requests")
@RequiredArgsConstructor
public class PrivateRequestController {

    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getRequestsOfCurrentUser(@PathVariable(name = "user-id") long userId) {
        PrivateRequestParam param = PrivateRequestParam.builder()
                .userId(userId)
                .build();
        return requestService.getRequestOfCurrentUser(param);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable(name = "user-id") long userId,
                                                 @RequestParam long eventId) {
        PrivateRequestParam param = PrivateRequestParam.builder()
                .userId(userId)
                .eventId(eventId)
                .build();
        return requestService.createRequest(param);
    }

    @PatchMapping("/{request-id}/cancel")
    public ParticipationRequestDto updateRequest(@PathVariable(name = "user-id") long userId,
                                                 @PathVariable(name = "request-id") long requestId) {
        PrivateRequestParam param = PrivateRequestParam.builder()
                .userId(userId)
                .requestId(requestId)
                .build();
        return requestService.updateRequest(param);
    }


}