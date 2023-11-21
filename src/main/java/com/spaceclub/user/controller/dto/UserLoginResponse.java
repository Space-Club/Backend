package com.spaceclub.user.controller.dto;

import com.spaceclub.user.service.vo.UserLoginInfo;

public record UserLoginResponse(Long userId, String accessToken, String refreshToken) {

    public static UserLoginResponse from(UserLoginInfo userLoginInfo) {
        return new UserLoginResponse(userLoginInfo.userId(), userLoginInfo.accessToken(), userLoginInfo.refreshToken());
    }

}
