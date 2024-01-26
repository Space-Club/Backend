package com.spaceclub.board.controller.dto;

import com.spaceclub.board.service.vo.CommentInfo;

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

    public static CommentResponse of(CommentInfo commentInfo, String bucketUrl) {
        return new CommentResponse(
                commentInfo.commentId(),
                commentInfo.content(),
                commentInfo.authorId(),
                commentInfo.author(),
                commentInfo.authorImageUrl() == null ? null : bucketUrl + commentInfo.authorImageUrl(),
                commentInfo.createdDate(),
                commentInfo.lastModifiedDate(),
                commentInfo.isPrivate()
        );
    }

}
