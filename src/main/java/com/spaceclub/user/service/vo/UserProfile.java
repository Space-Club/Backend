package com.spaceclub.user.service.vo;

import com.spaceclub.user.controller.dto.UserProfileResponse;
import com.spaceclub.user.domain.User;

public record UserProfile(
        String username,
        String phoneNumber,
        String profileImageUrl
) {

    public static UserProfile of(User user) {
        return new UserProfile(
                user.getUsername(),
                user.getPhoneNumber(),
                user.getProfileImageUrl()
        );
    }

    public UserProfileResponse toResponse() {
        return new UserProfileResponse(
                username,
                phoneNumber,
                profileImageUrl
        );
    }

}
