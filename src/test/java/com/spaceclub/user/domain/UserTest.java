package com.spaceclub.user.domain;

import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.user.UserTestFixture;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import static com.spaceclub.user.domain.Provider.KAKAO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class UserTest {

    @Test
    void 필수_정보를_입력하지_않고_신규유저라면_신규_유저_판별에_성공한다() {
        // given
        User user = User.builder()
                .oauthId("KAKAO1234")
                .provider(KAKAO)
                .status(Status.NOT_REGISTERED)
                .email("test@gmail.com")
                .build();

        // when
        boolean isNewMember = user.isNewMember();

        // then
        assertThat(isNewMember).isTrue();
    }

    @Test
    void 유저의_프로필_이미지를_null로_변경에_실패한다() {
        // given
        User user = UserTestFixture.user1();

        // when, then
        assertThatThrownBy(() -> user.changeProfileImageUrl(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 유저의_프로필_이미지_변경에_성공한다() {
        // given
        User user = UserTestFixture.user1();

        // when, then
        assertThatNoException()
                .isThrownBy(() -> user.changeProfileImageUrl("imageurl.com"));
    }

    @Test
    void 유저의_리프레시토큰_변경에_성공한다() {
        // given
        User user = UserTestFixture.user1();

        // when
        final String newRefreshToken = "newRefreshToken";
        User refreshTokenUpdatedUser = user.updateRefreshToken(newRefreshToken);

        // then
        assertThat(refreshTokenUpdatedUser.getRefreshToken()).isEqualTo(newRefreshToken);
    }

    @Test
    void 유저의_필수정보_변경에_성공한다() {
        // given
        User user = UserTestFixture.user1();

        // when
        final String usernameToUpdate = "업데이트된 멤버명";
        final String phoneNumberToUpdate = "01012346789";
        User updatedUser = user.updateRequiredInfo(usernameToUpdate, phoneNumberToUpdate);

        // then
        assertAll(
                () -> assertThat(updatedUser.getUsername()).isEqualTo(usernameToUpdate),
                () -> assertThat(updatedUser.getPhoneNumber()).isEqualTo(phoneNumberToUpdate)
        );
    }

    @Test
    void 유저의_닉네임이_입력과_같다면_유저_검증에_성공한다() {
        // given
        final User user = UserTestFixture.user1();
        final String username = user.getUsername();

        // when
        boolean isValid = user.isValid(username);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    void 유저의_닉네임이_입력과_다르다면_유저_검증에_실패한다() {
        // given
        final User user = UserTestFixture.user1();
        final String username = "다른 닉네임";

        // when
        boolean isValid = user.isValid(username);

        // then
        assertThat(isValid).isFalse();
    }

}
