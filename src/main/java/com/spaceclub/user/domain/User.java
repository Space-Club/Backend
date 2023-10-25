package com.spaceclub.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Builder;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
public class User {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long userid;

    @Embedded
    private RequiredInfo requiredInfo;

    @Column(nullable = false, unique = true)
    private String oauthUserName;

    @Enumerated(value = STRING)
    private Provider provider;

    @Embedded
    private Email email;

    @Lob
    private String refreshToken;

    @Builder
    private User(
            String nickname,
            String phoneNumber,
            String oauthUserName,
            Provider provider,
            Email email,
            String refreshToken
    ) {
        this.requiredInfo = new RequiredInfo(nickname, new PhoneNumber(phoneNumber));
        this.oauthUserName = oauthUserName;
        this.provider = provider;
        this.email = email;
        this.refreshToken = refreshToken;
    }

}
