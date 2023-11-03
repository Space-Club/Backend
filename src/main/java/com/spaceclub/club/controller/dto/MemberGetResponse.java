package com.spaceclub.club.controller.dto;

import com.spaceclub.club.domain.ClubUser;
import com.spaceclub.club.domain.ClubUserRole;
import lombok.Builder;

public record MemberGetResponse(
        Long id,
        String name,
        String profileImageUrl,
        ClubUserRole role
) {

    @Builder
    public MemberGetResponse(Long id, String name, String profileImageUrl, ClubUserRole role) {
        this.id = id;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
    }

    public static MemberGetResponse from(ClubUser clubUser) {

        return MemberGetResponse.builder()
                .id(clubUser.getUserId())
                .name(clubUser.getName())
                .profileImageUrl(clubUser.getProfileImageUrl())
                .role(clubUser.getRole())
                .build();
    }

}
