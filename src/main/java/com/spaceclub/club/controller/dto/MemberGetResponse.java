package com.spaceclub.club.controller.dto;

import com.spaceclub.club.domain.ClubUser;
import com.spaceclub.club.domain.ClubUserRole;
import lombok.Builder;

public record MemberGetResponse(
        Long id,
        String name,
        String image,
        ClubUserRole role
) {

    @Builder
    public MemberGetResponse(Long id, String name, String image, ClubUserRole role) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.role = role;
    }

    public static MemberGetResponse from(ClubUser clubUser) {

        return MemberGetResponse.builder()
                .id(clubUser.getUserId())
                .name(clubUser.getName())
                .image(clubUser.getImage())
                .role(clubUser.getRole())
                .build();
    }

}
