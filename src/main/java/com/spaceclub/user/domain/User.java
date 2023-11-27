package com.spaceclub.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

import static com.spaceclub.user.UserExceptionMessage.USER_CANNOT_WITHDRAW;
import static com.spaceclub.user.domain.Status.DELETED;
import static com.spaceclub.user.domain.Status.INACTIVE;
import static com.spaceclub.user.domain.Status.NOT_REGISTERED;
import static com.spaceclub.user.domain.Status.REGISTERED;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = PROTECTED)
public class User {

    private static final int GRACE_DAYS_OF_DELETION = 3;

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

    @Enumerated(STRING)
    private Provider provider;

    @Embedded
    private Email email;

    @Lob
    @Getter
    private String refreshToken;

    @Lob
    @Getter
    private String profileImageUrl;

    @Getter
    private LocalDateTime deletedAt;

    private User(
            Long id,
            RequiredInfo requiredInfo,
            Status status,
            String oauthUserName,
            Provider provider,
            Email email,
            String refreshToken,
            String profileImageUrl,
            LocalDateTime deletedAt
    ) {
        this.id = id;
        this.requiredInfo = requiredInfo;
        this.status = status;
        this.oauthUserName = oauthUserName;
        this.provider = provider;
        this.email = email;
        this.refreshToken = refreshToken;
        this.profileImageUrl = profileImageUrl;
        this.deletedAt = deletedAt;
    }

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
            String profileImageUrl,
            LocalDateTime deletedAt
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
        this.deletedAt = deletedAt;
    }

    private RequiredInfo generateRequiredInfo(String nickname, String phoneNumber) {
        if (nickname == null && phoneNumber == null) {
            return new RequiredInfo();
        }
        Assert.hasText(nickname, "닉네임은 필수입니다.");
        Assert.hasText(phoneNumber, "전화번호는 필수입니다.");

        return new RequiredInfo(nickname, new PhoneNumber(phoneNumber));
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

    public String getOauthId() {
        final String blank = "";
        return this.oauthUserName.replace(provider.name(), blank);
    }

    public User updateRequiredInfo(String name, String phoneNumber) {
        return new User(
                this.id,
                generateRequiredInfo(name, phoneNumber),
                REGISTERED,
                this.oauthUserName,
                this.provider,
                this.email,
                this.refreshToken,
                this.profileImageUrl,
                this.deletedAt
        );
    }

    public User changeProfileImageUrl(String profileUrl) {
        if (profileUrl == null) throw new IllegalArgumentException("프로필 이미지는 null이 될 수 없습니다.");

        return new User(
                this.id,
                this.requiredInfo,
                this.status,
                this.oauthUserName,
                this.provider,
                this.email,
                refreshToken,
                profileUrl,
                this.deletedAt
        );
    }

    public User updateRefreshToken(String refreshToken) {
        return new User(
                this.id,
                this.requiredInfo,
                this.status,
                this.oauthUserName,
                this.provider,
                this.email,
                refreshToken,
                this.profileImageUrl,
                this.deletedAt
        );
    }

    public boolean isValid(String username) {
        return this.getUsername().equals(username);
    }

    public User changeStatusToInactive() {
        return new User(
                this.id,
                this.requiredInfo,
                INACTIVE,
                this.oauthUserName,
                this.provider,
                this.email,
                this.refreshToken,
                this.profileImageUrl,
                LocalDateTime.now()
        );
    }

    public boolean isInactive() {
        return this.status.equals(INACTIVE);
    }

    public User changeStatusToRegistered() {
        return new User(
                this.id,
                this.requiredInfo,
                REGISTERED,
                this.oauthUserName,
                this.provider,
                this.email,
                this.refreshToken,
                this.profileImageUrl,
                null
        );
    }

    public User changeStatusToDeleted() {
        if (!this.status.equals(INACTIVE)){
            throw new IllegalStateException(USER_CANNOT_WITHDRAW.toString());
        }

        return new User(
                this.id,
                this.requiredInfo,
                DELETED,
                this.oauthUserName,
                this.provider,
                this.email,
                this.refreshToken,
                this.profileImageUrl,
                this.deletedAt
        );
    }

    public boolean isDeleted(LocalDateTime now) {
        if (deletedAt == null) {
            return false;
        }

        return this.status.equals(DELETED) || !this.deletedAt.plusDays(GRACE_DAYS_OF_DELETION).isAfter(now);
    }

}
