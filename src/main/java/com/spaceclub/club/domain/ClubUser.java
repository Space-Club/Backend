package com.spaceclub.club.domain;

import com.spaceclub.global.BaseTimeEntity;
import com.spaceclub.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Table(name = "club_user")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClubUser extends BaseTimeEntity {

    @Id
    @Column(name = "club_user_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Getter
    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    @Getter
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Getter
    @Enumerated(EnumType.STRING)
    private ClubUserRole role;

    @Builder
    public ClubUser(Long id, Club club, User user, ClubUserRole role) {
        this.id = id;
        this.club = club;
        this.user = user;
        this.role = role;
    }
    
    public ClubUser updateRole(ClubUserRole role) {
        return ClubUser.builder()
                .id(this.id)
                .club(this.club)
                .user(this.user)
                .role(role)
                .build();
    }

    public Long getUserId() {
        return user.getId();
    }

    public String getName() {
        return user.getName();
    }

    public String getImage() {
        return user.getImage();
    }

}
