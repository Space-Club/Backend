package com.spaceclub.user.service.vo;

import com.spaceclub.user.controller.dto.UserProfileResponse;

public record UserProfileInfo(
        String username,
        String phoneNumber,
        String profileImageUrl
) {

    public UserProfileResponse toResponse() {
        return new UserProfileResponse(
                username,
                phoneNumber,
                profileImageUrl
        );
    }

}
