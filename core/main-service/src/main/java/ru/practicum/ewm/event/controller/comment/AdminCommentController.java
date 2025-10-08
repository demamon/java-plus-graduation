package ru.practicum.ewm.event.controller.comment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.comment.CommentDto;
import ru.practicum.ewm.event.dto.comment.UpdateCommentDto;
import ru.practicum.ewm.event.param.AdminCommentParam;
import ru.practicum.ewm.event.service.EventService;

@RestController
@RequestMapping(path = "/admin/events/{event-id}/comments/{comment-id}")
@RequiredArgsConstructor
public class AdminCommentController {
    private final EventService eventService;

    @GetMapping
    public CommentDto getCommentById(@PathVariable(name = "event-id") long eventId,
                                     @PathVariable(name = "comment-id") long commentId) {
        AdminCommentParam param = AdminCommentParam.builder()
                .eventId(eventId)
                .commentId(commentId)
                .build();
        return eventService.getCommentById(param);
    }

    @PatchMapping
    public CommentDto updateComment(@Valid @RequestBody UpdateCommentDto comment,
                                    @PathVariable(name = "event-id") long eventId,
                                    @PathVariable(name = "comment-id") long commentId) {
        AdminCommentParam param = AdminCommentParam.builder()
                .eventId(eventId)
                .commentId(commentId)
                .comment(comment)
                .build();
        return eventService.updateComment(param);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeComment(@PathVariable(name = "event-id") long eventId,
                              @PathVariable(name = "comment-id") long commentId) {
        AdminCommentParam param = AdminCommentParam.builder()
                .eventId(eventId)
                .commentId(commentId)
                .build();
        eventService.removeComment(param);
    }
}
