package com.spaceclub.global.oauth.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "oauth.kakao.access")
public class KakaoOauthProperties {

    private final String clientId;
    private final String redirectUrl;
    private final String clientSecret;

}

