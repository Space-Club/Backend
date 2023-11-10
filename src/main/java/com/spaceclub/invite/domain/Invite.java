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

    @Builder
    public Invite(String code, Club club) {
        this.code = code;
        this.club = club;
    }

    public Invite updateCode(String code) {
        this.code = code;
        return this;
    }

}
