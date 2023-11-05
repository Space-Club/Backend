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

    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 10;

    @Getter
    @Column(nullable = false, length = 10)
    private String name;

    @Embedded
    private PhoneNumber phoneNumber;

    public RequiredInfo(String name, PhoneNumber phoneNumber) {
        validate(name);
        this.name = name.trim();
        this.phoneNumber = phoneNumber;
    }

    private void validate(String name) {
        Assert.hasText(name, "이름을 입력해 주세요.");
        Assert.isTrue(isValidRange(name.trim()), "이름은 2자 이상 10자 이하로 입력해 주세요.");
    }

    private boolean isValidRange(String name) {
        return name.length() >= MIN_NAME_LENGTH && name.length() <= MAX_NAME_LENGTH;
    }

    public boolean isNotFilled() {
        return name == null || phoneNumber == null;
    }

    public String getPhoneNumber() {
        return phoneNumber.getPhoneNumber();
    }

}
