package com.spaceclub.user.domain;

import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
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
    @ValueSource(strings = {"", " ", "    "})
    void 닉네임이_null이거나_공백일_경우_필수정보_객체_생성에_생성에_실패한다(String invalidNickname) {
        // when, then
        assertThatThrownBy(() -> new RequiredInfo(invalidNickname, validPhoneNumber))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "{index}. nickname : {arguments}")
    @ValueSource(strings = {"1", "가", "가나다라마바사아자차카", "12345678901", "가   "})
    void 양끝_공백을_제거한_닉네임이_2글자이상_10글자이하_범위에서_벗어나면_필수정보_객체_생성에_생성에_실패한다(String invalidNickname) {
        // when, then
        assertThatThrownBy(() -> new RequiredInfo(invalidNickname, validPhoneNumber))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "{index}. nickname : {arguments}")
    @ValueSource(strings = {"가나", "     가나    ", "가  나"})
    void 양끝_공백을_제거한_닉네임이_2글자이상_10글자이하_범위에_있으면_필수정보_객체_생성에_생성에_성공한다(String validNickname) {
        // when, then
        assertThatNoException()
                .isThrownBy(() -> new RequiredInfo(validNickname, validPhoneNumber));
    }

}
