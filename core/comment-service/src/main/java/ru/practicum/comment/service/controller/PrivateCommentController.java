package ru.practicum.comment.service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.service.service.CommentService;
import ru.practicum.interaction.api.dto.comment.CommentDto;
import ru.practicum.interaction.api.dto.comment.NewCommentDto;
import ru.practicum.interaction.api.dto.comment.PrivateCommentParam;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{user-id}/events/{event-id}/comments")
@RequiredArgsConstructor
@Slf4j
public class PrivateCommentController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getCommentsOfUser(@PathVariable(name = "user-id") long userId,
                                              @PathVariable(name = "event-id") long eventId,
                                              @RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "10") int size) {
        PrivateCommentParam param = PrivateCommentParam.builder()
                .userId(userId)
                .eventId(eventId)
                .from(from)
                .size(size)
                .build();
        return commentService.getCommentsOfUser(param);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@Valid @RequestBody NewCommentDto comment,
                                    @PathVariable(name = "user-id") long userId,
                                    @PathVariable(name = "event-id") long eventId) {
        log.debug("получаем запрос на создание комментария к событию");
        PrivateCommentParam param = PrivateCommentParam.builder()
                .newComment(comment)
                .userId(userId)
                .eventId(eventId)
                .build();
        return commentService.createComment(param);
    }
}