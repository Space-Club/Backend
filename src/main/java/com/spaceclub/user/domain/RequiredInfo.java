package com.spaceclub.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import static lombok.AccessLevel.PROTECTED;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = PROTECTED)
public class RequiredInfo {

    public static final int MIN_NICKNAME_LENGTH = 2;
    public static final int MAX_NICKNAME_LENGTH = 10;


    @Column(nullable = false)
    private String nickname;

    @Embedded
    private PhoneNumber phoneNumber;

    public RequiredInfo(String nickname, PhoneNumber phoneNumber) {
        validate(nickname);
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
    }

    private void validate(String nickname) {
        Assert.hasText(nickname, "닉네임을 입력해 주세요.");
        Assert.isTrue(!nickname.contains(" "), "닉네임에 공백을 포함할 수 없습니다.");
        Assert.isTrue(isInValidRange(nickname), "닉네임은 2자 이상 10자 이하로 입력해 주세요.");
    }

    private boolean isInValidRange(String nickname) {
        return nickname.length() >= MIN_NICKNAME_LENGTH && nickname.length() <= MAX_NICKNAME_LENGTH;
    }

}
