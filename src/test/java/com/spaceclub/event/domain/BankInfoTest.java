package com.spaceclub.event.domain;

import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.spaceclub.event.EventTestFixture.bankInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class BankInfoTest {

    private static final String BANK_NAME_MAX_LENGTH = "값".repeat(21);

    private static final String BANK_ACCOUNT_NUMBER_MAX_LENGTH = "값".repeat(31);

    static Stream<Arguments> invalidBankName() {
        return Stream.of(
                Arguments.arguments(""),
                Arguments.arguments("           "),
                Arguments.arguments(BANK_NAME_MAX_LENGTH)
        );
    }

    static Stream<Arguments> invalidBankAccountNumber() {
        return Stream.of(
                Arguments.arguments(""),
                Arguments.arguments("           "),
                Arguments.arguments(BANK_ACCOUNT_NUMBER_MAX_LENGTH)
        );
    }

    @Test
    void 은행_정보_생성에_성공한다() {
        assertThat(bankInfo()).isNotNull();
    }

    @MethodSource("invalidBankName")
    @ParameterizedTest(name = "{index}. bankName : [{arguments}]")
    void 은행_정보가_유효하지_않으면_생성에_실패한다(String bankName) {
        assertThatThrownBy(() ->
                BankInfo.builder()
                        .bankName(bankName)
                        .bankAccountNumber(bankInfo().getBankAccountNumber())
                        .build())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @MethodSource("invalidBankAccountNumber")
    @ParameterizedTest(name = "{index}. bankAccountNumber : [{arguments}]")
    void 은행_계좌번호가_유효하지_않으면_생성에_실패한다(String bankAccountNumber) {
        assertThatThrownBy(() ->
                BankInfo.builder()
                        .bankName(bankInfo().getBankName())
                        .bankAccountNumber(bankAccountNumber)
                        .build())
                .isInstanceOf(IllegalArgumentException.class);
    }

}
