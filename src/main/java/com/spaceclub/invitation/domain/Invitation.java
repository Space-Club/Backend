package com.spaceclub.invitation.domain;

import com.spaceclub.club.domain.Club;
import com.spaceclub.global.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Invitation extends BaseTimeEntity {

    @Id
    @Column(name = "invitation_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "club_id")
    private Club club;

    @Column(length = 6)
    private String code;

    public Invitation(Long id, Club club, String code) {
        this.id = id;
        this.club = club;
        this.code = code;
    }

    public Invitation assignCode(String code) {
        return new Invitation(
                this.id,
                this.club,
                code
        );
    }

}
