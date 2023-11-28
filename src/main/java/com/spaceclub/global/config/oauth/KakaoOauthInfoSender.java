package com.spaceclub.global.config.oauth;

import com.spaceclub.global.config.oauth.vo.KakaoTokenInfo;
import com.spaceclub.global.config.oauth.vo.KakaoUserInfo;
import com.spaceclub.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static com.spaceclub.global.exception.GlobalExceptionCode.KAKAO_LOGOUT_FAIL;
import static com.spaceclub.global.exception.GlobalExceptionCode.KAKAO_UNLINK_FAIL;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@Component
@RequiredArgsConstructor
public class KakaoOauthInfoSender {

    private final RestTemplate restTemplate;
    private final KakaoOauthProperties kakaoProperties;
    private final KakaoRequestUrlProperties urlProperties;

    public KakaoUserInfo getUserInfo(String accessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(APPLICATION_FORM_URLENCODED);
        httpHeaders.setBearerAuth(accessToken);

        HttpEntity<Object> requestEntity = new HttpEntity<>(httpHeaders);

        return restTemplate.exchange(
                urlProperties.requestInfo(),
                POST,
                requestEntity,
                KakaoUserInfo.class
        ).getBody();
    }

    public KakaoTokenInfo getAccessTokenInfo(String code) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", kakaoProperties.grantType());
        body.add("client_id", kakaoProperties.clientId());
        body.add("redirect_uri", kakaoProperties.redirectUrl());
        body.add("client_secret", kakaoProperties.clientSecret());
        body.add("code", code);

        HttpEntity<Object> requestEntity = new HttpEntity<>(body, httpHeaders);

        return restTemplate.exchange(
                urlProperties.requestToken(),
                POST,
                requestEntity,
                KakaoTokenInfo.class
        ).getBody();
    }

    public void logout(User user) {
        HttpEntity<Object> requestEntity = generateRequest(user);

        Long id = restTemplate.exchange(
                urlProperties.logout(),
                POST,
                requestEntity,
                Long.class
        ).getBody();

        if (id == null) throw new IllegalArgumentException(KAKAO_LOGOUT_FAIL.toString());
        if (id != Long.parseLong(user.getOauthId())) throw new IllegalArgumentException(KAKAO_LOGOUT_FAIL.toString());
    }

    public void unlink(User user) {
        HttpEntity<Object> requestEntity = generateRequest(user);

        Long id = restTemplate.exchange(
                urlProperties.unlink(),
                POST,
                requestEntity,
                Long.class
        ).getBody();

        if (id == null) throw new IllegalArgumentException(KAKAO_UNLINK_FAIL.toString());
        if (id != Long.parseLong(user.getOauthId())) throw new IllegalArgumentException(KAKAO_UNLINK_FAIL.toString());
    }

    private HttpEntity<Object> generateRequest(User user) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(APPLICATION_FORM_URLENCODED);
        httpHeaders.set(AUTHORIZATION, kakaoProperties.adminKeyPrefix() + " " + kakaoProperties.adminKey());

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("target_id_type", "user_id");
        body.add("target_id", user.getOauthId());

        return new HttpEntity<>(body, httpHeaders);
    }

}
