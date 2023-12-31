package com.spaceclub.event.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.spaceclub.event.EventExceptionMessage.INVALID_EVENT_BANK_ACCOUNT_NUMBER;
import static com.spaceclub.event.EventExceptionMessage.INVALID_EVENT_BANK_NAME;

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
        if (bankName != null) {
            boolean invalidBankNameLength = bankName.length() > BANK_NAME_MAX_LENGTH || bankName.isBlank();
            if (invalidBankNameLength) throw new IllegalArgumentException(INVALID_EVENT_BANK_NAME.toString());
        }

        if (bankAccountNumber != null) {
            boolean invalidBankAccountNumberLength = bankAccountNumber.length() > BANK_ACCOUNT_NUMBER_MAX_LENGTH || bankAccountNumber.isBlank();
            if (invalidBankAccountNumberLength)
                throw new IllegalArgumentException(INVALID_EVENT_BANK_ACCOUNT_NUMBER.toString());
        }
    }

}
