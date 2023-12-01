package com.spaceclub.user.domain;

import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class EmailTest {

    @ParameterizedTest(name = "{index}. email : {arguments}")
    @NullSource
    @ValueSource(strings = {"", " "})
    void 이메일이_null이거나_이메일_생성에_실패한다(String email) {
        // given
        boolean emailConsent = true;

        // when, then
        assertThatThrownBy(() -> new Email(email,emailConsent))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "{index}. email : {arguments}")
    @ValueSource(strings = {"123", "wrongEmail", "wrongEmail@", "wrongEmail@com", "wrongEmail@com."})
    void 이메일의_형식이_잘못되었을_경우_이메일_생성에_실패한다(String email) {
        // given
        boolean emailConsent = true;

        // when, then
        assertThatThrownBy(() -> new Email(email, emailConsent))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 이메일의_형식이_올바르면_이메일_생성에_성공한다() {
        // given
        String validEmail = "valid@gmail.com";
        boolean emailConsent = true;

        // when, then
        assertThatNoException()
                .isThrownBy(() -> new Email(validEmail, emailConsent));
    }

}
