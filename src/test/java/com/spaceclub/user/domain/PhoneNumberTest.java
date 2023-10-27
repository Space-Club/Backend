package com.spaceclub.user.domain;

import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class PhoneNumberTest {

    @ParameterizedTest(name = "{index}. Phone Number : {arguments}")
    @NullSource
    @ValueSource(strings = {"", " "})
    void 휴대폰_번호가_null이거나_공백이면_휴대폰_번호_생성에_실패한다(String invalidPhoneNumber) {
        // when, then
        assertThatThrownBy(() -> new PhoneNumber(invalidPhoneNumber))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "{index}. Phone Number : {arguments}")
    @ValueSource(strings = {"1234567890, 가나다라, abcd, 01012345678900"})
    void 휴대폰_번호의_형식이_잘못되었을_경우_휴대폰_번호_생성에_실패한다(String invalidPhoneNumber) {
        // when, then
        assertThatThrownBy(() -> new PhoneNumber(invalidPhoneNumber))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "{index}. Phone Number : {arguments}")
    @ValueSource(strings = {"010-1234-5678", "01012345678", "0101235678", "010-12345678", "010-123-5678",
            "011-1234-5678", "019-1234-5678"})
    void 휴대폰_번호의_형식이_올바르면_휴대폰_번호_생성에_성공한다(String validPhoneNumber) {
        // when, then
        assertThatNoException()
                .isThrownBy(() -> new PhoneNumber(validPhoneNumber));
    }

    @Test
    void 휴대폰_번호에_대시가_있을경우_제거_후_저장에_성공한다() {
        // given
        String phoneNumberWithDash = "010-1234-5678";

        // when
        PhoneNumber phoneNumber = new PhoneNumber(phoneNumberWithDash);

        // then
        assertThat(phoneNumber).isEqualTo(new PhoneNumber("01012345678"));
    }

}
