package com.spaceclub.user.controller.dto;

public record UserLoginResponse(String accessToken) {

    public static UserLoginResponse from(String accessToken) {
        return new UserLoginResponse(accessToken);
    }

}
