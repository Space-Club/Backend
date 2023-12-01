package com.spaceclub.club.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClubNotice {

    @Id
    @Getter
    @Column(name = "club_notice_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    @Getter
    @Column(length = 1000)
    private String notice;

    @Builder
    public ClubNotice(Long id, Club club, String notice) {
        this.id = id;
        this.club = club;
        this.notice = notice;
    }

    public ClubNotice updateNotice(String notice) {
        this.notice = notice;
        return this;
    }

}
