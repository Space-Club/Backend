package com.spaceclub.global.oauth.config;

import com.spaceclub.global.oauth.config.vo.KakaoTokenInfo;
import com.spaceclub.global.oauth.config.vo.KakaoUserInfo;
import com.spaceclub.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@Component
@RequiredArgsConstructor
public class KakaoOauthInfoSender {

    private static final String REQUEST_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String REQUEST_INFO_URL = "https://kapi.kakao.com/v2/user/me";
    private static final String GRANT_TYPE = "authorization_code";
    private static final String LOGOUT_URL = "https://kapi.kakao.com/v1/user/logout";
    private static final String ADMIN_KEY_PREFIX = "KakaoaAK ";

    private final RestTemplate restTemplate;
    private final KakaoOauthProperties kakaoProperties;

    public KakaoUserInfo getUserInfo(String accessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(APPLICATION_FORM_URLENCODED);
        httpHeaders.setBearerAuth(accessToken);

        HttpEntity<Object> requestEntity = new HttpEntity<>(httpHeaders);

        return restTemplate.exchange(
                REQUEST_INFO_URL,
                POST,
                requestEntity,
                KakaoUserInfo.class
        ).getBody();
    }

    public KakaoTokenInfo getAccessTokenInfo(String code) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", GRANT_TYPE);
        body.add("client_id", kakaoProperties.getClientId());
        body.add("redirect_uri", kakaoProperties.getRedirectUrl());
        body.add("client_secret", kakaoProperties.getClientSecret());
        body.add("code", code);

        HttpEntity<Object> requestEntity = new HttpEntity<>(body, httpHeaders);

        return restTemplate.exchange(
                REQUEST_TOKEN_URL,
                POST,
                requestEntity,
                KakaoTokenInfo.class
        ).getBody();
    }

    public void logout(User user) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(APPLICATION_FORM_URLENCODED);
        httpHeaders.set(HttpHeaders.AUTHORIZATION, ADMIN_KEY_PREFIX + kakaoProperties.getAdminKey());

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("target_id_type", "user_id");
        body.add("target_id", user.getOauthId());

        HttpEntity<Object> requestEntity = new HttpEntity<>(body, httpHeaders);

        Long id = restTemplate.exchange(
                LOGOUT_URL,
                POST,
                requestEntity,
                Long.class
        ).getBody();

        if (id == null) throw new IllegalArgumentException("로그아웃 실패");
        if (id != Long.parseLong(user.getOauthId())) throw new IllegalArgumentException("로그아웃 실패");
    }

}
