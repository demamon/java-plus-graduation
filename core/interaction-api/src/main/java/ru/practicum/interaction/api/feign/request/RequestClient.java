package ru.practicum.interaction.api.feign.request;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interaction.api.dto.request.ParticipationRequestDto;
import ru.practicum.interaction.api.enums.request.RequestState;


import java.util.List;

@FeignClient(name = "request-service")
public interface RequestClient {
    @GetMapping("/users/{user-id}/requests/event/{event-id}")
    List<ParticipationRequestDto> findUserEventRequests(@PathVariable(name = "user-id") Long userId,
                                                        @PathVariable(name = "event-id") Long eventId);

    @GetMapping("/users/{user-id}/requests/findById/{request-id}")
    ParticipationRequestDto getRequest(@PathVariable(name = "user-id") Long userId,
                                       @PathVariable(name = "request-id") Long requestId);

    @PostMapping("/users/{user-id}/requests/save")
    void saveRequest(@PathVariable(name = "user-id") Long userId,
                     @RequestBody ParticipationRequestDto request);

    @GetMapping("/users/{user-id}/requests/{eventId}/check-user-confirmed/{userId}")
    boolean checkExistStatusRequest(@PathVariable Long eventId,@PathVariable Long userId,
                                    @RequestParam RequestState state);
}

