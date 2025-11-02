package ru.practicum.request.service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interaction.api.dto.request.PrivateRequestParam;
import ru.practicum.interaction.api.dto.request.ParticipationRequestDto;
import ru.practicum.interaction.api.enums.request.RequestState;
import ru.practicum.request.service.service.RequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{user-id}/requests")
@RequiredArgsConstructor
@Slf4j
public class PrivateRequestController {

    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getRequestsOfCurrentUser(@PathVariable(name = "user-id") long userId) {
        PrivateRequestParam param = PrivateRequestParam.builder()
                .userId(userId)
                .build();
        return requestService.getRequestOfCurrentUser(param);
    }

    @GetMapping("/event/{event-id}")
    List<ParticipationRequestDto> findUserEventRequests(@PathVariable(name = "user-id") Long userId,
                                                        @PathVariable(name = "event-id") Long eventId) {
        log.debug("мы в контроллере после feign, userId = {}, eventId = {}", userId, eventId);
        return requestService.findUserEventRequests(userId, eventId);
    }

    @GetMapping("/findById/{request-id}")
    ParticipationRequestDto getRequest(@PathVariable(name = "user-id") Long userId,
                                       @PathVariable(name = "request-id") Long requestId) {
        return requestService.getRequest(requestId);
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

    @PostMapping("/save")
    void saveRequest(@PathVariable(name = "user-id") long userId,
                     @RequestBody ParticipationRequestDto request) {
        requestService.saveRequest(request);
    }

    @GetMapping("/{eventId}/check-user-confirmed/{userId}")
    public boolean checkExistStatusRequest(@PathVariable Long eventId,@PathVariable Long userId,
                                                               @RequestParam RequestState state) {
        return requestService.checkExistStatusRequest(eventId, userId, state);
    }


}