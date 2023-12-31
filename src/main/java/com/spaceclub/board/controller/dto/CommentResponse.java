package com.spaceclub.board.controller.dto;

import com.spaceclub.board.controller.domain.Comment;

import java.time.LocalDateTime;

public record CommentResponse(
        Long commentId,
        String content,
        Long authorId,
        String author,
        String authorImageUrl,
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate,
        boolean isPrivate
) {

    public static CommentResponse of(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getAuthorId(),
                "authorName",
                "authorImageUrl",
                LocalDateTime.of(2023, 12, 31, 12, 0, 0),
                LocalDateTime.of(2023, 12, 31, 12, 0, 0),
                comment.isPrivate()
        );
    }

}
