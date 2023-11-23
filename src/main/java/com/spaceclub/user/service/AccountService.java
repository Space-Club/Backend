package com.spaceclub.user.service;

import com.spaceclub.global.jwt.JwtManager;
import com.spaceclub.global.oauth.config.KakaoOauthInfoSender;
import com.spaceclub.global.oauth.config.vo.KakaoTokenInfo;
import com.spaceclub.global.oauth.config.vo.KakaoUserInfo;
import com.spaceclub.user.domain.Email;
import com.spaceclub.user.domain.Provider;
import com.spaceclub.user.domain.User;
import com.spaceclub.user.repository.UserRepository;
import com.spaceclub.user.service.vo.UserLoginInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.spaceclub.user.UserExceptionMessage.USER_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountService {

    private final JwtManager jwtManager;
    private final UserRepository userRepository;
    private final KakaoOauthInfoSender kakaoOauthInfoSender;

    @Transactional
    public UserLoginInfo loginUser(String code) {
        User kakaoUser = createKakaoUser(code);

        if (kakaoUser.isNewMember()) {
            return UserLoginInfo.from(kakaoUser.getId(), "", "");
        }

        String accessToken = jwtManager.createAccessToken(kakaoUser.getId(), kakaoUser.getUsername());
        String refreshToken = jwtManager.createRefreshToken(kakaoUser.getId());

        return UserLoginInfo.from(kakaoUser.getId(), accessToken, refreshToken);
    }


    private User createKakaoUser(String code) {
        KakaoTokenInfo accessTokenInfo = kakaoOauthInfoSender.getAccessTokenInfo(code);
        String accessToken = accessTokenInfo.accessToken();
        KakaoUserInfo userInfo = kakaoOauthInfoSender.getUserInfo(accessToken);

        Email email = new Email(userInfo.email());
        String oauthUsername = Provider.KAKAO.name() + userInfo.id();

        return userRepository.findByEmailAndOauthUserName(email, oauthUsername)
                .orElseGet(() -> userRepository.save(userInfo.toUser()));
    }

    public void logout(Long userId) {
        User user = getUser(userId);

        kakaoOauthInfoSender.logout(user);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND.toString()));
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = getUser(userId);

        userRepository.delete(user); // 추후 정책 반영에 따라 수정 예정

        kakaoOauthInfoSender.unlink(user);
    }


    @Transactional
    public UserLoginInfo createAccount(Long userId) {
        User user = getUser(userId);

        String accessToken = jwtManager.createAccessToken(user.getId(), user.getUsername());
        String refreshToken = jwtManager.createRefreshToken(user.getId());

        return UserLoginInfo.from(user.getId(), accessToken, refreshToken);
    }


}
