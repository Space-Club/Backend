package com.spaceclub.user.controller.dto;

import com.spaceclub.club.service.vo.ClubInfo;
import lombok.Builder;

public record UserClubGetResponse(Long id, String logoImageUrl, String name) {

    @Builder
    public UserClubGetResponse {
    }

    public static UserClubGetResponse from(ClubInfo clubInfo) {
        return UserClubGetResponse.builder()
                .id(clubInfo.id())
                .logoImageUrl(clubInfo.logoImageUrl())
                .name(clubInfo.name())
                .build();
    }

}
