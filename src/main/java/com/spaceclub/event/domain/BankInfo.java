package com.spaceclub.event.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BankInfo {

    private static final int BANK_NAME_MAX_LENGTH = 20;

    private static final int BANK_ACCOUNT_NUMBER_MAX_LENGTH = 30;

    @Getter
    private String bankName;

    @Getter
    private String bankAccountNumber;

    @Builder
    public BankInfo(String bankName, String bankAccountNumber) {
        validate(bankName, bankAccountNumber);
        this.bankName = bankName;
        this.bankAccountNumber = bankAccountNumber;
    }

    private void validate(String bankName, String bankAccountNumber) {
        Assert.isTrue(bankName.length() <= BANK_NAME_MAX_LENGTH && !bankName.isBlank(), "은행명은 1~20자 사이의 길이입니다.");
        Assert.isTrue(bankAccountNumber.length() <= BANK_ACCOUNT_NUMBER_MAX_LENGTH && !bankAccountNumber.isBlank(), "은행 계좌번호는 1~30자 사이의 길이입니다.");
    }

}
