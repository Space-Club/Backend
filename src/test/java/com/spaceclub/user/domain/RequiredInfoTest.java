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
class RequiredInfoTest {

    private final PhoneNumber validPhoneNumber = new PhoneNumber("010-1234-5678");

    @ParameterizedTest(name = "{index}. value : {arguments}")
    @NullSource
    @ValueSource(strings = {"", " ", "    "})
    void 이름이_null이거나_공백일_경우_필수정보_객체_생성에_생성에_실패한다(String invalidName) {
        // when, then
        assertThatThrownBy(() -> new RequiredInfo(invalidName, validPhoneNumber))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "{index}. name : {arguments}")
    @ValueSource(strings = {"1", "가", "가나다라마바사아자차카", "12345678901", "가   "})
    void 양끝_공백을_제거한_이름이_2글자이상_10글자이하_범위에서_벗어나면_필수정보_객체_생성에_생성에_실패한다(String invalidName) {
        // when, then
        assertThatThrownBy(() -> new RequiredInfo(invalidName, validPhoneNumber))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "{index}. name : {arguments}")
    @ValueSource(strings = {"가나", "     가나    ", "가  나"})
    void 양끝_공백을_제거한_이름이_2글자이상_10글자이하_범위에_있으면_필수정보_객체_생성에_생성에_성공한다(String validName) {
        // when, then
        assertThatNoException()
                .isThrownBy(() -> new RequiredInfo(validName, validPhoneNumber));
    }

    @Test
    void 필수정보를_입력하지_않으면_입력하지_않았다는_검증에_성공한다() {
        // given
        RequiredInfo requiredInfo = new RequiredInfo();

        // when
        boolean isRequiredInfoEmpty = requiredInfo.isNotFilled();

        // then
        assertThat(isRequiredInfoEmpty).isTrue();
    }

    @Test
    void 필수정보를_입력하면_입력하지_않았다는_검증에_실패한다() {
        // given
        PhoneNumber validPhoneNumber = new PhoneNumber("010-1234-5678");
        RequiredInfo requiredInfo = new RequiredInfo("validName", validPhoneNumber);

        // when
        boolean isRequiredInfoEmpty = requiredInfo.isNotFilled();

        // then
        assertThat(isRequiredInfoEmpty).isFalse();
    }

}
