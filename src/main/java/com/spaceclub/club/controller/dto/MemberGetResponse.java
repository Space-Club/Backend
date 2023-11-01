package com.spaceclub.club.controller.dto;

import com.spaceclub.club.domain.ClubUserRole;

public record MemberGetResponse(
        Long id,
        String name,
        String image,
        ClubUserRole role
) {

}
