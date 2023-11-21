package com.spaceclub.user.service.vo;

public record UserLoginInfo(Long userId, String accessToken, String refreshToken) {

    public static UserLoginInfo from(Long userId, String accessToken, String refreshToken) {
        return new UserLoginInfo(userId, accessToken, refreshToken);
    }

}
