package com.spaceclub.club.service.vo;

import com.spaceclub.club.domain.ClubUser;
import com.spaceclub.club.domain.ClubUserRole;
import com.spaceclub.user.service.vo.UserProfileInfo;
import lombok.Builder;

import java.util.Comparator;

public record MemberGet(
        Long id,
        String name,
        String profileImageUrl,
        ClubUserRole role
) {

    @Builder
    public MemberGet {
    }

    public static Comparator<MemberGet> memberComparator = Comparator
            .comparing((MemberGet response) -> response.role().getSortPriority())
            .thenComparing(MemberGet::name);

    public static MemberGet from(ClubUser clubUser, UserProfileInfo userProfile) {
        return MemberGet.builder()
                .id(clubUser.getUserId())
                .name(userProfile.username())
                .profileImageUrl(userProfile.profileImageUrl())
                .role(clubUser.getRole())
                .build();
    }

}
