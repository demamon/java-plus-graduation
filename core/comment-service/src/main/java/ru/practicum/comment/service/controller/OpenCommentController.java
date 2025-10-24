package ru.practicum.comment.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.service.service.CommentService;
import ru.practicum.interaction.api.dto.comment.CommentDto;
import ru.practicum.interaction.api.dto.comment.OpenCommentParam;


import java.util.Collection;

@RestController
@RequestMapping(path = "/events/{event-id}/comments")
@RequiredArgsConstructor
public class OpenCommentController {
    private final CommentService commentService;

    @GetMapping
    public Collection<CommentDto> getComments(@PathVariable(name = "event-id") long eventId,
                                              @RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "10") int size) {
        OpenCommentParam param = OpenCommentParam.builder()
                .eventId(eventId)
                .from(from)
                .size(size)
                .build();
        return commentService.getComments(param);
    }
}
