package com.spaceclub.user.controller.dto;

public record UserLoginResponse(Long userId, String accessToken) {

    public static UserLoginResponse from(Long userId, String accessToken) {
        return new UserLoginResponse(userId, accessToken);
    }

}
