package com.spaceclub.club.service.vo;

import com.spaceclub.club.domain.ClubUser;
import com.spaceclub.club.domain.ClubUserRole;
import com.spaceclub.user.service.vo.UserProfile;
import lombok.Builder;

import java.util.Comparator;

public record MemberGetInfo(
        Long id,
        String name,
        String profileImageUrl,
        ClubUserRole role
) {

    @Builder
    public MemberGetInfo {
    }

    public static Comparator<MemberGetInfo> memberComparator = Comparator
            .comparing((MemberGetInfo response) -> response.role().getSortPriority())
            .thenComparing(MemberGetInfo::name);

    public static MemberGetInfo from(ClubUser clubUser, UserProfile userProfile) {
        return MemberGetInfo.builder()
                .id(clubUser.getUserId())
                .name(userProfile.username())
                .profileImageUrl(userProfile.profileImageUrl())
                .role(clubUser.getRole())
                .build();
    }

}
