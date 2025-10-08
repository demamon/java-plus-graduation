package ru.practicum.ewm.event.controller.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.comment.CommentDto;
import ru.practicum.ewm.event.param.OpenCommentParam;
import ru.practicum.ewm.event.service.EventService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/events/{event-id}/comments")
@RequiredArgsConstructor
public class OpenCommentController {
    private final EventService eventService;

    @GetMapping
    public Collection<CommentDto> getComments(@PathVariable(name = "event-id") long eventId,
                                              @RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "10") int size) {
        OpenCommentParam param = OpenCommentParam.builder()
                .eventId(eventId)
                .from(from)
                .size(size)
                .build();
        return eventService.getComments(param);
    }
}
