package com.spaceclub.user.domain;

import com.spaceclub.event.domain.EventUser;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
public class User {

    @Id
    @Getter
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_id")
    private Long id;

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

    @Lob
    @Getter
    private String profileImageUrl;

    @OneToMany(mappedBy = "user")
    private List<EventUser> events = new ArrayList<>();

    @Builder
    private User(
            Long id,
            String name,
            String phoneNumber,
            String oauthId,
            Provider provider,
            String email,
            String refreshToken,
            String profileImageUrl
    ) {
        this.id = id;
        this.requiredInfo = new RequiredInfo(name, new PhoneNumber(phoneNumber));
        this.oauthUserName = generateOauthUsername(oauthId, provider);
        this.provider = provider;
        this.email = new Email(email);
        this.refreshToken = refreshToken;
        this.profileImageUrl = profileImageUrl;
    }

    public String getName() {
        return requiredInfo.getName();
    }

    private String generateOauthUsername(String oauthId, Provider provider) {
        return provider.name() + oauthId;
    }

}
