package com.spaceclub.global.config.oauth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.kakao.access")
public record KakaoOauthProperties(
        String clientId,
        String redirectUrl,
        String clientSecret,
        String adminKey,
        String grantType,
        String adminKeyPrefix
) {

}

