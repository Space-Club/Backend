package com.spaceclub.club.domain;

import com.spaceclub.global.BaseTimeEntity;
import com.spaceclub.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

import static jakarta.persistence.GenerationType.IDENTITY;

@Table(name = "club_user")
@Entity
public class ClubUser extends BaseTimeEntity {

    @Id
    @Column(name = "club_user_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Getter
    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 16, nullable = false)
    private String role;

}
