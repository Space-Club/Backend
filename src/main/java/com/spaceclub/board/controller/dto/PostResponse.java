package com.spaceclub.board.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spaceclub.board.controller.domain.Post;
import lombok.Builder;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PostResponse(
        Long postId,
        String title,
        String content,
        Long authorId,
        String author,
        String authorImageUrl,
        String postImageUrl,
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate
) {

    @Builder
    public PostResponse {
    }

    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthorId(),
                "authorName",
                "authorImageUrl",
                post.getPostImageUrl(),
                LocalDateTime.of(2023, 12, 31, 12, 0, 0),
                LocalDateTime.of(2023, 12, 31, 12, 0, 0)
        );
    }

}
