package com.spaceclub.user.domain;

import com.spaceclub.event.domain.EventUser;
import com.spaceclub.form.domain.FormOptionUser;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

import static com.spaceclub.user.domain.Status.NOT_REGISTERED;
import static com.spaceclub.user.domain.Status.REGISTERED;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = PROTECTED)
public class User {

    @Id
    @Getter
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Embedded
    private RequiredInfo requiredInfo = new RequiredInfo();

    @Getter
    @Enumerated(STRING)
    private Status status;

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

    @OneToMany(mappedBy = "user")
    private List<FormOptionUser> formOptionUsers = new ArrayList<>();

    @Builder
    private User(
            Long id,
            String name,
            String phoneNumber,
            Status status,
            String oauthId,
            Provider provider,
            String email,
            String refreshToken,
            String profileImageUrl
    ) {
        Assert.notNull(status, "유저 상태는 필수입니다.");
        this.id = id;
        this.requiredInfo = generateRequiredInfo(name, phoneNumber);
        this.status = status;
        this.oauthUserName = generateOauthUsername(oauthId, provider);
        this.provider = provider;
        this.email = new Email(email);
        this.refreshToken = refreshToken;
        this.profileImageUrl = profileImageUrl;
    }

    private RequiredInfo generateRequiredInfo(String nickname, String phoneNumber) {
        if (nickname == null && phoneNumber == null) {
            return new RequiredInfo();
        }
        Assert.hasText(nickname, "닉네임은 필수입니다.");
        Assert.hasText(phoneNumber, "전화번호는 필수입니다.");

        return new RequiredInfo(nickname, new PhoneNumber(phoneNumber));
    }

    public String getName() {
        return requiredInfo.getName();
    }

    public boolean isNewMember() {
        return this.requiredInfo.isNotFilled() && status.equals(NOT_REGISTERED);
    }

    private String generateOauthUsername(String oauthId, Provider provider) {
        return provider.name() + oauthId;
    }

    public String getUsername() {
        return requiredInfo.getName();
    }

    public String getPhoneNumber() {
        return requiredInfo.getPhoneNumber();
    }

    public User updateRequiredInfo(String name, String phoneNumber) {
        this.requiredInfo = generateRequiredInfo(name, phoneNumber);
        this.status = REGISTERED;
        return this;
    }

}
