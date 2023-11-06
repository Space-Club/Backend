package com.spaceclub.user.controller.dto;

public record UserProfileResponse(
        String username,
        String phoneNumber,
        String profileImageUrl
) {

}
