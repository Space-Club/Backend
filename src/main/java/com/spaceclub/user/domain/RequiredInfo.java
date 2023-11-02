package com.spaceclub.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import static lombok.AccessLevel.PROTECTED;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = PROTECTED)
public class RequiredInfo {

    private static final int MIN_NICKNAME_LENGTH = 2;
    private static final int MAX_NICKNAME_LENGTH = 10;

    @Getter
    @Column(nullable = false, columnDefinition = "varchar(10) default UNKNOWN")
    private String nickname;

    @Embedded
    private PhoneNumber phoneNumber;

    public RequiredInfo(String nickname, PhoneNumber phoneNumber) {
        validate(nickname);
        this.nickname = nickname.trim();
        this.phoneNumber = phoneNumber;
    }

    private void validate(String nickname) {
        Assert.hasText(nickname, "닉네임을 입력해 주세요.");
        Assert.isTrue(isValidRange(nickname.trim()), "닉네임은 2자 이상 10자 이하로 입력해 주세요.");
    }

    private boolean isValidRange(String nickname) {
        return nickname.length() >= MIN_NICKNAME_LENGTH && nickname.length() <= MAX_NICKNAME_LENGTH;
    }

    public String getPhoneNumber() {
        return this.phoneNumber.getPhoneNumber();
    }

}
