package com.spaceclub.board.service.vo;

import com.spaceclub.board.controller.domain.Comment;
import com.spaceclub.user.service.vo.UserProfile;

import java.time.LocalDateTime;

public record CommentInfo(
        Long commentId,
        String content,
        Long authorId,
        String author,
        String authorImageUrl,
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate,
        boolean isPrivate
)
{

    public static CommentInfo of(Comment comment, UserProfile userProfile) {
        return new CommentInfo(
                comment.getId(),
                comment.getContent(),
                comment.getAuthorId(),
                userProfile.username(),
                userProfile.profileImageUrl(),
                comment.getCreatedAt(),
                comment.getLastModifiedAt(),
                comment.isPrivate()
        );
    }

}
