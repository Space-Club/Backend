package com.spaceclub.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.util.regex.Pattern;

import static lombok.AccessLevel.PROTECTED;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = PROTECTED)
public class PhoneNumber {

    private static final String REGEX = "^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$";

    @Getter
    @Column(nullable = false)
    private String phoneNumber;

    public PhoneNumber(String phoneNumber) {
        validatePhoneNumber(phoneNumber);
        this.phoneNumber = phoneNumber;
    }

    private void validatePhoneNumber(String phoneNumber) {
        Assert.hasText(phoneNumber, "올바른 전화번호를 입력해 주세요.");
        Assert.isTrue(Pattern.matches(REGEX, phoneNumber), "올바른 전화번호를 입력해 주세요.");
    }

}

