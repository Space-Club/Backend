package com.spaceclub.board.controller.dto;

import com.spaceclub.board.service.vo.PostInfo;

import java.time.LocalDateTime;

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

    public static PostResponse from(PostInfo postInfo, String bucketUrl) {
        return new PostResponse(
                postInfo.postId(),
                postInfo.title(),
                postInfo.content(),
                postInfo.authorId(),
                postInfo.author(),
                postInfo.authorImageUrl(),
                postInfo.postImageUrl(bucketUrl),
                postInfo.createdDate(),
                postInfo.lastModifiedDate()
        );
    }

}
