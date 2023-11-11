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
    public Invite(String code, Club club, LocalDateTime expiredAt) {
        this.code = code;
        this.club = club;
        this.expiredAt = expiredAt;
    }

    public Invite updateCode(String code) {
        this.code = code;
        return this;
    }

    public boolean isExpired() {
        LocalDateTime expiredAt = this.expiredAt;
        LocalDateTime now = LocalDateTime.now();

        return now.isAfter(expiredAt);
    }

}
