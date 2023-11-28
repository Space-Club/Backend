package com.spaceclub.user.service.vo;

import com.spaceclub.user.controller.dto.UserProfileResponse;
import com.spaceclub.user.domain.User;

public record UserProfile(
        String username,
        String phoneNumber,
        String profileImageUrl
) {

    public static UserProfile of(User user, String bucketUrl) {
        return new UserProfile(
                user.getUsername(),
                user.getPhoneNumber(),
                getProfileImageUrl(user, bucketUrl)
        );
    }

    private static String getProfileImageUrl(User user, String profileImageUrlPrefix) {
        String profileImageUrl = user.getProfileImageUrl();
        if (profileImageUrl == null) {
            return null;
        }
        return profileImageUrlPrefix + profileImageUrl;
    }

    public UserProfileResponse toResponse() {
        return new UserProfileResponse(
                username,
                phoneNumber,
                profileImageUrl
        );
    }

}
