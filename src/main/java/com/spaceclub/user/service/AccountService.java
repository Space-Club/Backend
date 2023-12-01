package com.spaceclub.user.service;

import com.spaceclub.global.bad_word_filter.BadWordFilter;
import com.spaceclub.global.config.oauth.KakaoOauthInfoSender;
import com.spaceclub.global.config.oauth.vo.KakaoTokenInfo;
import com.spaceclub.global.config.oauth.vo.KakaoUserInfo;
import com.spaceclub.global.jwt.JwtManager;
import com.spaceclub.global.mail.MailEvent;
import com.spaceclub.user.domain.Email;
import com.spaceclub.user.domain.Provider;
import com.spaceclub.user.domain.User;
import com.spaceclub.user.repository.UserRepository;
import com.spaceclub.user.service.vo.UserLoginInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.spaceclub.user.UserExceptionMessage.USER_CANNOT_WITHDRAW;
import static com.spaceclub.user.UserExceptionMessage.USER_DELETED;
import static com.spaceclub.user.UserExceptionMessage.USER_NOT_FOUND;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountService {

    private static final String BLANK = "";

    private final JwtManager jwtManager;

    private final UserRepository userRepository;

    private final KakaoOauthInfoSender kakaoOauthInfoSender;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public UserLoginInfo loginUser(String code, LocalDateTime now) {
        User kakaoUser = createKakaoUser(code);

        if (kakaoUser.isNewMember()) {
            return UserLoginInfo.from(kakaoUser.getId(), BLANK, BLANK);
        }

        String accessToken = jwtManager.createAccessToken(kakaoUser.getId(), kakaoUser.getUsername());
        String refreshToken = jwtManager.createRefreshToken(kakaoUser.getId());

        checkWhenUserStatusIsInactive(kakaoUser, now);

        return UserLoginInfo.from(kakaoUser.getId(), accessToken, refreshToken);
    }

    private User createKakaoUser(String code) {
        KakaoTokenInfo accessTokenInfo = kakaoOauthInfoSender.getAccessTokenInfo(code);
        String accessToken = accessTokenInfo.accessToken();
        KakaoUserInfo userInfo = kakaoOauthInfoSender.getUserInfo(accessToken);
        boolean emailConsent = true;

        Email email = new Email(userInfo.email(), emailConsent);
        String oauthUsername = Provider.KAKAO.name() + userInfo.id();

        return userRepository.findByEmailAndOauthUserName(email, oauthUsername)
                .orElseGet(() -> userRepository.save(userInfo.toUser()));
    }

    private void checkWhenUserStatusIsInactive(User kakaoUser, LocalDateTime now) {
        if (kakaoUser.isInactive()) {
            User user = kakaoUser.changeStatusToRegistered();
            userRepository.save(user);

            return;
        }
        if (kakaoUser.isDeleted(now)) {
            throw new IllegalStateException(USER_DELETED.toString());
        }
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
    public void deleteUser(Long userId, int clubCount) {
        validateRegisteredClubCount(clubCount);

        User inactiveUser = getUser(userId).changeStatusToInactive();
        userRepository.save(inactiveUser);
    }

    private void validateRegisteredClubCount(int clubCount) {
        if (clubCount != 0) {
            throw new IllegalStateException(USER_CANNOT_WITHDRAW.toString());
        }
    }

    @Transactional
    public UserLoginInfo createAccount(Long userId) {
        User user = getUser(userId);
        BadWordFilter.filter(user.getUsername());
        if (user.emailConsent()) {
            eventPublisher.publishEvent(MailEvent.welcomeEvent(user.getEmail()));
        }

        return generateToken(userId);
    }

    @Transactional
    public UserLoginInfo generateToken(Long userId) {
        User user = getUser(userId);

        String accessToken = jwtManager.createAccessToken(user.getId(), user.getUsername());
        String refreshToken = jwtManager.createRefreshToken(user.getId());

        return UserLoginInfo.from(user.getId(), accessToken, refreshToken);
    }

    public boolean isAuthenticatedUser(Long userId, String username) {
        User user = getUser(userId);

        if (!user.isValid(username)) {
            throw new IllegalStateException(USER_NOT_FOUND.toString());
        }

        return true;
    }

    @Transactional
    public void changeEmailConsent(Long userId, boolean emailConsent) {
        User user = getUser(userId);

        userRepository.save(user.changeEmailConsent(emailConsent));
    }

}
