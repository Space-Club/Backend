package com.spaceclub.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import static com.spaceclub.user.UserExceptionMessage.INVALID_NAME;
import static lombok.AccessLevel.PROTECTED;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = PROTECTED)
public class RequiredInfo {

    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 10;

    @Getter
    @Column(length = 10)
    private String name;

    @Embedded
    private PhoneNumber phoneNumber;

    public RequiredInfo(String name, PhoneNumber phoneNumber) {
        validate(name);
        this.name = name.trim();
        this.phoneNumber = phoneNumber;
    }

    private void validate(String name) {
        Assert.hasText(name, INVALID_NAME.toString());
        Assert.isTrue(isValidRange(name.trim()), INVALID_NAME.toString());
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
