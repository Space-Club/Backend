package com.spaceclub.invite.domain;

import com.spaceclub.club.domain.Club;
import com.spaceclub.global.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Invite extends BaseTimeEntity {

    public static final int INVITE_LINK_VALID_HOURS = 48;

    @Id
    @Column(name = "invite_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Getter
    @Column(unique = true)
    private String code;

    @Getter
    @OneToOne
    @JoinColumn(name = "club_id", unique = true)
    private Club club;

    @Getter
    private LocalDateTime expiredAt;

    @Builder
    public Invite(Long id, String code, Club club, LocalDateTime expiredAt, LocalDateTime createdAt) {
        this.id = id;
        this.code = code;
        this.club = club;
        this.expiredAt = expiredAt;
        this.createdAt = createdAt;
    }

    public Invite updateCode(String code) {
        return Invite.builder()
                .id(this.id)
                .club(this.club)
                .code(code)
                .expiredAt(LocalDateTime.now().plusHours(INVITE_LINK_VALID_HOURS))
                .createdAt(this.createdAt)
                .build();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiredAt);
    }

}
