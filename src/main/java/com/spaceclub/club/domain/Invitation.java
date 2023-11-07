package com.spaceclub.club.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Embeddable
@EqualsAndHashCode(of = "invitationCode")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Invitation {

    @Getter
    private String invitationCode;

    @Getter
    private LocalDateTime invitationCodeGeneratedAt;

    @Builder
    public Invitation(String invitationCode, LocalDateTime invitationCodeGeneratedAt) {
        this.invitationCode = invitationCode;
        this.invitationCodeGeneratedAt = invitationCodeGeneratedAt;
    }

}
