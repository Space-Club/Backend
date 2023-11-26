package com.spaceclub.user.controller.dto;

import com.spaceclub.user.service.vo.RequiredProfile;

public record UserRequiredInfoRequest(Long userId, String name, String phoneNumber) {

    public RequiredProfile toRequiredProfile() {
        return new RequiredProfile(name, phoneNumber);
    }

}
