package com.spaceclub.club.controller.dto;

import com.spaceclub.club.domain.ClubUser;
import com.spaceclub.club.domain.ClubUserRole;
import com.spaceclub.user.service.vo.UserProfileInfo;
import lombok.Builder;

import java.util.Comparator;

public record MemberGetResponse(
        Long id,
        String name,
        String profileImageUrl,
        ClubUserRole role
) {

    @Builder
    public MemberGetResponse {
    }

    public static Comparator<MemberGetResponse> memberComparator = Comparator
            .comparing((MemberGetResponse response) -> response.role().getSortPriority())
            .thenComparing(MemberGetResponse::name);

    public static MemberGetResponse from(ClubUser clubUser, UserProfileInfo userProfile) {
        return MemberGetResponse.builder()
                .id(clubUser.getUserId())
                .name(userProfile.username())
                .profileImageUrl(userProfile.profileImageUrl())
                .role(clubUser.getRole())
                .build();
    }

}
