package com.spaceclub.user.controller.dto;

import com.spaceclub.user.domain.User;

public record UserLoginResponse(String accessToken, boolean isNewMember) {

    public static UserLoginResponse from(User user) {
        return new UserLoginResponse("accessToken",user.isNewMember());
    }

}
