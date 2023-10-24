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
class EmailTest {

    @ParameterizedTest(name = "{index}. email : {arguments}")
    @NullSource
    @ValueSource(strings = {"", " "})
    void 이메일이_null이거나_공백이면_예외를_발생한다(String email) {
        // when, then
        assertThatThrownBy(() -> new Email(email))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "{index}. email : {arguments}")
    @ValueSource(strings = {"123", "wrongEmail", "wrongEmail@", "wrongEmail@com", "wrongEmail@com."})
    void 이메일의_형식이_잘못되었을_경우_예외를_발생한다(String email) {
        // when, then
        assertThatThrownBy(() -> new Email(email))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 이메일의_형식이_올바르면_예외를_발생하지_않는다() {
        // given
        String validEmail = "valid@gmail.com";

        // when, then
        assertThatNoException()
                .isThrownBy(() -> new Email(validEmail));
    }

}
