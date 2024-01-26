package com.spaceclub.board.service.vo;

import com.spaceclub.board.controller.domain.Post;
import com.spaceclub.user.service.vo.UserProfile;

import java.time.LocalDateTime;

public record PostInfo(
        Long postId,
        String title,
        String content,
        Long authorId,
        String author,
        String authorImageUrl,
        String postImageUrl,
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate
)
{

    public static PostInfo of(Post post, UserProfile userProfile) {
        return new PostInfo(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthorId(),
                userProfile.username(),
                userProfile.profileImageUrl(),
                post.getPostImageUrl(),
                post.getCreatedAt(),
                post.getLastModifiedAt()
        );
    }
}
