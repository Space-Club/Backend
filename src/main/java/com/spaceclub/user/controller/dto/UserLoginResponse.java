package com.spaceclub.user.controller.dto;

public record UserLoginResponse(Long userId, String accessToken, String refreshToken) {

    public static UserLoginResponse from(Long userId, String accessToken, String refreshToken) {
        return new UserLoginResponse(userId, accessToken, refreshToken);
    }

}
