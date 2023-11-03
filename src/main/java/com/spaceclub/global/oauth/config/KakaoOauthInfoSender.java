package com.spaceclub.global.oauth.config;

import com.spaceclub.global.oauth.config.vo.KakaoTokenInfo;
import com.spaceclub.global.oauth.config.vo.KakaoUserInfo;
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

}
