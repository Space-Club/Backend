package com.spaceclub.club.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClubNotice {

    @Id
    @Column(name = "club_notice_id")
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    @Getter
    private String notice;

    public ClubNotice(String notice) {
        this.notice = notice;
    }

}
