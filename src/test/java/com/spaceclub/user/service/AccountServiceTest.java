package com.spaceclub.user.service;

import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.global.config.oauth.KakaoOauthInfoSender;
import com.spaceclub.global.config.oauth.vo.KakaoTokenInfo;
import com.spaceclub.global.config.oauth.vo.KakaoUserInfo;
import com.spaceclub.global.jwt.JwtManager;
import com.spaceclub.user.UserTestFixture;
import com.spaceclub.user.domain.User;
import com.spaceclub.user.repository.UserRepository;
import com.spaceclub.user.service.vo.UserLoginInfo;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class AccountServiceTest {

    @InjectMocks
    AccountService accountService;

    @Mock
    JwtManager jwtManager;

    @Mock
    UserRepository userRepository;

    @Mock
    KakaoOauthInfoSender kakaoOauthInfoSender;

    @Test
    void 필수정보를_작성하지않은_신규유저이면_토큰을_빈값으로_반환에_성공한다() {
        // given
        KakaoTokenInfo kakaoTokenInfo = new KakaoTokenInfo("tokenType", "accessToken", 1, "refreshToken", 1);
        KakaoUserInfo.KakaoAccount kakaoAccount = new KakaoUserInfo.KakaoAccount(true, true, "test@gmail.com");
        KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(1L, kakaoAccount);
        User user = UserTestFixture.notRegisteredUser();

        given(kakaoOauthInfoSender.getAccessTokenInfo(any())).willReturn(kakaoTokenInfo);
        given(kakaoOauthInfoSender.getUserInfo(any())).willReturn(kakaoUserInfo);
        given(userRepository.findByEmailAndOauthUserName(any(), any())).willReturn(Optional.of(user));

        // when
        UserLoginInfo actual = accountService.loginUser("code");
        UserLoginInfo expected = new UserLoginInfo(3L, "", "");

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 유저가_신규_유저가_아니라면_엑세스토큰과_리프레시토큰을_생성해_반환하는데_성공한다() {
        // given
        KakaoTokenInfo kakaoTokenInfo = new KakaoTokenInfo("tokenType", "accessToken", 1, "refreshToken", 1);
        KakaoUserInfo.KakaoAccount kakaoAccount = new KakaoUserInfo.KakaoAccount(true, true, "test@gmail.com");
        KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(1L, kakaoAccount);
        User user = UserTestFixture.user1().changeStatusToInactive();

        given(kakaoOauthInfoSender.getAccessTokenInfo(any())).willReturn(kakaoTokenInfo);
        given(kakaoOauthInfoSender.getUserInfo(any())).willReturn(kakaoUserInfo);
        given(userRepository.findByEmailAndOauthUserName(any(), any())).willReturn(Optional.of(user));
        given(jwtManager.createAccessToken(any(), any())).willReturn("accessToken");
        given(jwtManager.createRefreshToken(any())).willReturn("refreshToken");

        // when
        UserLoginInfo actual = accountService.loginUser("code");
        UserLoginInfo expected = new UserLoginInfo(1L, "accessToken", "refreshToken");

        // then
        assertThat(actual).isEqualTo(expected);
    }

}
