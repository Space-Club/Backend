package com.spaceclub.club.controller.dto;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.domain.Invitation;

import java.time.LocalDateTime;

public record ClubCreateRequest(
        String name,
        String info
) {

    public Club toEntity(String logoImageUrl) {
        return Club.builder()
                .name(name)
                .info(info)
                .invitation(InvitationCreateRequest.toEntity())
                .logoImageUrl(logoImageUrl)
                .build();
    }

    record InvitationCreateRequest(
            String invitationCode,
            LocalDateTime invitationCodeGeneratedAt
    ) {

        public static Invitation toEntity() {
            return Invitation.builder()
                    .invitationCode(null)
                    .invitationCodeGeneratedAt(null)
                    .build();
        }

    }

}
