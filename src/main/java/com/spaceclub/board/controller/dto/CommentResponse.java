package com.spaceclub.board.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spaceclub.board.service.vo.CommentInfo;

import java.time.LocalDateTime;

public record CommentResponse(
        Long commentId,
        String content,
        Long authorId,
        String author,
        String authorImageUrl,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdDate,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime lastModifiedDate,
        boolean isPrivate
) {

    public static CommentResponse of(CommentInfo commentInfo) {
        return new CommentResponse(
                commentInfo.commentId(),
                commentInfo.content(),
                commentInfo.authorId(),
                commentInfo.author(),
                commentInfo.authorImageUrl(),
                commentInfo.createdDate(),
                commentInfo.lastModifiedDate(),
                commentInfo.isPrivate()
        );
    }

}
