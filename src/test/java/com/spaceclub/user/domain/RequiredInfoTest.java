package com.spaceclub.user.domain;

import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.ReplaceUnderscores.class)
class RequiredInfoTest {

    private final PhoneNumber validPhoneNumber = new PhoneNumber("010-1234-5678");

    @ParameterizedTest(name = "{index}. value : {arguments}")
    @NullSource
    @ValueSource(strings = {"", " ", "    ", "a ", "a b", "    a   "})
    void 닉네임이_null이거나_공백을_포함하면_필수정보_객체_생성에_생성에_실패한다(String invalidNickname) {
        // when, then
        assertThatThrownBy(() -> new RequiredInfo(invalidNickname, validPhoneNumber))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "{index}. nickname : {arguments}")
    @ValueSource(strings = {"1", "가", "가나다라마바사아자차카", "12345678901"})
    void 닉네임이_2글자이상_10글자이하_범위에서_벗어나면_필수정보_객체_생성에_생성에_실패한다(String invalidNickname) {
        // when, then
        assertThatThrownBy(() -> new RequiredInfo(invalidNickname, validPhoneNumber))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 닉네임이_2글자이상_10글자이하_범위에_있으면_필수정보_객체_생성에_생성에_성공한다() {
        // given
        String validNickname = "가나";

        // when, then
        assertThatNoException()
                .isThrownBy(() -> new RequiredInfo(validNickname, validPhoneNumber));
    }

}
