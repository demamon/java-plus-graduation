package ru.practicum.comment.service.service;

import ru.practicum.interaction.api.dto.comment.AdminCommentParam;
import ru.practicum.interaction.api.dto.comment.CommentDto;
import ru.practicum.interaction.api.dto.comment.OpenCommentParam;
import ru.practicum.interaction.api.dto.comment.PrivateCommentParam;

import java.util.List;

public interface CommentService {

    List<CommentDto> getCommentsOfUser(PrivateCommentParam param);

    CommentDto createComment(PrivateCommentParam param);

    List<CommentDto> getComments(OpenCommentParam param);

    CommentDto getCommentById(AdminCommentParam param);

    CommentDto updateComment(AdminCommentParam param);

    void removeComment(AdminCommentParam param);
}
