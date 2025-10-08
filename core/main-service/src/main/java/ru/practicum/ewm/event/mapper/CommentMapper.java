package ru.practicum.ewm.event.mapper;

import ru.practicum.ewm.event.dto.comment.CommentDto;
import ru.practicum.ewm.event.dto.comment.NewCommentDto;
import ru.practicum.ewm.event.model.Comment;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CommentMapper {
    private static String datePattern = "yyyy-MM-dd HH:mm:ss";
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);

    public static CommentDto mapToCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .description(comment.getDescription())
                .event(comment.getEvent().getId())
                .user(comment.getUser().getId())
                .created(comment.getCreated().format(formatter))
                .build();
    }

    public static List<CommentDto> mapToCommentDto(Iterable<Comment> comments) {
        List<CommentDto> commentsResult = new ArrayList<>();

        for (Comment comment : comments) {
            commentsResult.add(mapToCommentDto(comment));
        }

        return commentsResult;
    }

    public static Comment mapFromRequest(NewCommentDto comment) {
        return new Comment(
                comment.getDescription()
        );
    }
}