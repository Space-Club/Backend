package com.spaceclub.board.controller.dto;

public record CommentRequest(
        String content,
        boolean isPrivate
) {

}
