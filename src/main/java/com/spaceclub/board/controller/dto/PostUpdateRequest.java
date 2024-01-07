package com.spaceclub.board.controller.dto;

public record PostUpdateRequest(
        String title,
        String content,
        boolean doesPostImageExist
) {

}
