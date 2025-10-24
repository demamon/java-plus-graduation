package ru.practicum.comment.service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.service.service.CommentService;
import ru.practicum.interaction.api.dto.comment.AdminCommentParam;
import ru.practicum.interaction.api.dto.comment.CommentDto;
import ru.practicum.interaction.api.dto.comment.UpdateCommentDto;

@RestController
@RequestMapping(path = "/admin/events/{event-id}/comments/{comment-id}")
@RequiredArgsConstructor
public class AdminCommentController {
    private final CommentService commentService;

    @GetMapping
    public CommentDto getCommentById(@PathVariable(name = "event-id") long eventId,
                                     @PathVariable(name = "comment-id") long commentId) {
        AdminCommentParam param = AdminCommentParam.builder()
                .eventId(eventId)
                .commentId(commentId)
                .build();
        return commentService.getCommentById(param);
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
        return commentService.updateComment(param);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeComment(@PathVariable(name = "event-id") long eventId,
                              @PathVariable(name = "comment-id") long commentId) {
        AdminCommentParam param = AdminCommentParam.builder()
                .eventId(eventId)
                .commentId(commentId)
                .build();
        commentService.removeComment(param);
    }
}
