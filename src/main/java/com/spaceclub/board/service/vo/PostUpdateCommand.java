package com.spaceclub.board.service.vo;

public enum PostUpdateCommand {
    UPDATE_ONLY_CONTENT,
    UPDATE_CONTENT_AND_IMAGE,
    UPDATE_CONTENT_AND_ERASE_IMAGE;

    public static PostUpdateCommand getCommand(boolean doesImageExists, String postImageUrl) {
        if (doesImageExists && postImageUrl != null) {
            return UPDATE_CONTENT_AND_IMAGE;
        }
        if (doesImageExists) {
            return UPDATE_ONLY_CONTENT;
        }
        if (postImageUrl == null) {
            return UPDATE_CONTENT_AND_ERASE_IMAGE;
        }
        throw new IllegalArgumentException("잘못된 요청입니다.");
    }

}
