package com.spaceclub.user.domain;

import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import static com.spaceclub.user.domain.Provider.KAKAO;
import static org.assertj.core.api.Assertions.assertThat;

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

}
