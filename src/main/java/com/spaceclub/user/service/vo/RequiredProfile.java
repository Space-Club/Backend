package com.spaceclub.user.service.vo;

import com.spaceclub.user.controller.dto.UserProfileUpdateRequest;

public record RequiredProfile(String name, String phoneNumber, String email) {

    public static RequiredProfile of(UserProfileUpdateRequest request) {
        return new RequiredProfile(request.name(), request.phoneNumber(), request.email());
    }

}
