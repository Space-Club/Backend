package com.spaceclub.user.controller.dto;

public record UserProfileUpdateRequest(
        String name,
        String phoneNumber
) {

}
