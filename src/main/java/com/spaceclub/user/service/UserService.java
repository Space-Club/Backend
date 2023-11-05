package com.spaceclub.user.service;

import com.spaceclub.event.domain.Event;
import com.spaceclub.event.repository.EventUserRepository;
import com.spaceclub.global.oauth.config.KakaoOauthInfoSender;
import com.spaceclub.global.oauth.config.vo.KakaoTokenInfo;
import com.spaceclub.global.oauth.config.vo.KakaoUserInfo;
import com.spaceclub.user.domain.Provider;
import com.spaceclub.user.domain.User;
import com.spaceclub.user.repository.UserRepository;
import com.spaceclub.user.service.vo.UserRequiredInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final KakaoOauthInfoSender kakaoOauthInfoSender;
    private final EventUserRepository eventUserRepository;
    private final UserRepository userRepository;

    public Page<Event> findAllEventPages(Long userId, Pageable pageable) {
        return eventUserRepository.findAllByUserId(userId, pageable);
    }

    public String findEventStatus(Long userId, Event event) {
        return eventUserRepository.findEventStatusByUserId(userId, event);
    }

    @Transactional
    public User createKakaoUser(String code) {
        KakaoTokenInfo accessTokenInfo = kakaoOauthInfoSender.getAccessTokenInfo(code);
        String accessToken = accessTokenInfo.accessToken();
        KakaoUserInfo userInfo = kakaoOauthInfoSender.getUserInfo(accessToken);

        return userRepository.findByEmailAndOauthUserName(userInfo.email(), Provider.KAKAO.name() + userInfo.id())
                .orElseGet(() -> userRepository.save(userInfo.toUser()));
    }

    public User findByUser(Long userId, UserRequiredInfo userRequiredInfo) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        User updatedUser = user.updateRequiredInfo(userRequiredInfo.name(), userRequiredInfo.phoneNumber());

        return userRepository.save(updatedUser);
    }

}


