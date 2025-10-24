package ru.practicum.comment.service.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.service.exception.NotFoundException;
import ru.practicum.comment.service.mapper.CommentMapper;
import ru.practicum.comment.service.model.Comment;
import ru.practicum.comment.service.model.QComment;
import ru.practicum.comment.service.repository.CommentRepository;
import ru.practicum.interaction.api.dto.comment.*;
import ru.practicum.interaction.api.feign.event.EventClient;
import ru.practicum.interaction.api.feign.user.UserClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserClient userClient;
    private final EventClient eventClient;

    @Override
    public List<CommentDto> getCommentsOfUser(PrivateCommentParam param) {
        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        Pageable page = PageRequest.of(param.getFrom(), param.getSize(), sortById);

        QComment qComment = QComment.comment;
        List<BooleanExpression> conditions = new ArrayList<>();

        conditions.add(QComment.comment.eventId.eq(param.getEventId()));
        conditions.add(QComment.comment.userId.eq(param.getUserId()));

        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .get();

        return CommentMapper.mapToCommentDto(commentRepository.findAll(finalCondition, page)
        );
    }

    @Transactional
    @Override
    public CommentDto createComment(PrivateCommentParam param) {
        log.debug("получили параметры для создания комментария к событию {}", param);
        NewCommentDto commentFromRequest = param.getNewComment();
        Long userId = userClient.getUserFull(param.getUserId()).getId();
        Long eventId = eventClient.getEvent(param.getEventId()).getId();
        Comment newComment = commentRepository.save(CommentMapper.mapFromRequest(commentFromRequest));
        newComment.setUserId(userId);
        newComment.setEventId(eventId);
        log.debug("имеем новый комментарий перед маппером {}", newComment);
        return CommentMapper.mapToCommentDto(newComment);
    }

    @Override
    public List<CommentDto> getComments(OpenCommentParam param) {
        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        Pageable page = PageRequest.of(param.getFrom(), param.getSize(), sortById);

        QComment qComment = QComment.comment;
        List<BooleanExpression> conditions = new ArrayList<>();

        conditions.add(QComment.comment.eventId.eq(param.getEventId()));

        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .get();

        return CommentMapper.mapToCommentDto(commentRepository.findAll(finalCondition, page));
    }

    @Override
    public CommentDto getCommentById(AdminCommentParam param) {
        Comment comment = commentRepository.findById(param.getCommentId()).orElseThrow(
                () -> new NotFoundException(String.format("Комментарий id = %d не найден", param.getCommentId()))
        );
        return CommentMapper.mapToCommentDto(comment);
    }

    @Transactional
    @Override
    public CommentDto updateComment(AdminCommentParam param) {
        Comment oldComment = commentRepository.findById(param.getCommentId()).orElseThrow(
                () -> new NotFoundException(String.format("Комментарий id = %d не найден", param.getCommentId()))
        );
        UpdateCommentDto commentOnUpdate = param.getComment();
        if (commentOnUpdate.hasDescription()) {
            oldComment.setDescription(commentOnUpdate.getDescription());
        }
        commentRepository.save(oldComment);
        return CommentMapper.mapToCommentDto(oldComment);
    }

    @Transactional
    @Override
    public void removeComment(AdminCommentParam param) {
        long commentId = param.getCommentId();
        if (commentRepository.findById(commentId).isEmpty())
            throw new NotFoundException(String.format("Комментарий id = %d не найден", commentId));
        commentRepository.deleteById(commentId);
    }
}
