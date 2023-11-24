package com.spaceclub.global.config.oauth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.kakao.url")
public record KakaoRequestUrlProperties(
        String requestToken,
        String requestInfo,
        String logout,
        String unlink
) {

}
