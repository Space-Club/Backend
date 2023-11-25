package com.spaceclub.user.domain;

import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.user.UserTestFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static com.spaceclub.user.UserExceptionMessage.USER_CANNOT_WITHDRAW;
import static com.spaceclub.user.domain.Provider.KAKAO;
import static com.spaceclub.user.domain.Status.DELETED;
import static com.spaceclub.user.domain.Status.INACTIVE;
import static com.spaceclub.user.domain.Status.NOT_REGISTERED;
import static com.spaceclub.user.domain.Status.REGISTERED;
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
                .status(NOT_REGISTERED)
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

    @Test
    void 유저의_상태를_INACTIVE로_변경에_성공한다() {
        // given
        final User user = UserTestFixture.user1();

        // when
        User inactiveUser = user.changeStatusToInactive();

        // then
        assertAll(
                () -> assertThat(inactiveUser.getStatus()).isEqualTo(INACTIVE),
                () -> assertThat(inactiveUser.getStatus()).isNotEqualTo(REGISTERED)
        );
    }

    @Test
    void 유저의_상태를_DELETED로_변경에_성공한다() {
        // given
        Long userId = 1L;
        final User user = UserTestFixture.deletedUser(userId, INACTIVE, LocalDateTime.now());

        // when
        User deletedUser = user.changeStatusToDeleted();

        // then
        assertAll(
                () -> assertThat(deletedUser.getStatus()).isEqualTo(DELETED),
                () -> assertThat(deletedUser.getStatus()).isNotEqualTo(INACTIVE)
        );
    }

    @Test
    void REGISTERED상태의_유저는_유저의_상태를_DELETED로_변경에_실패한다() {
        // given
        final User user = UserTestFixture.user1();

        // when, then
        Assertions.assertThatThrownBy(user::changeStatusToDeleted)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(USER_CANNOT_WITHDRAW.toString());
    }

    @ParameterizedTest(name = "{index}: 삭제된 시간: {0}")
    @MethodSource("deletedDateTime")
    void 유저의_삭제되었는지_상태확인에_성공한다(LocalDateTime deletedAt, boolean expected) {
        // given
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.of(2023, 12, 4, 0, 0, 0);
        final User user = UserTestFixture.deletedUser(userId, INACTIVE, deletedAt);

        // when
        boolean isDeleted = user.isDeleted(now);

        // then
        assertThat(isDeleted).isEqualTo(expected);
    }

    public static Stream<Arguments> deletedDateTime() {
        return Stream.of(
                // 삭제 안된 계정
                Arguments.of(LocalDateTime.of(2023, 12, 1, 0, 0, 1), false),
                Arguments.of(LocalDateTime.of(2023, 12, 1, 1, 0, 0), false),
                Arguments.of(LocalDateTime.of(2023, 12, 2, 0, 0, 0), false),
                Arguments.of(LocalDateTime.of(2023, 12, 3, 0, 0, 0), false),
                Arguments.of(LocalDateTime.of(2023, 12, 4, 0, 0, 0), false),

                // 삭제 된 계정
                Arguments.of(LocalDateTime.of(2023, 12, 1, 0, 0, 0), true),
                Arguments.of(LocalDateTime.of(2023, 11, 30, 23, 59, 59), true)
        );
    }

}
