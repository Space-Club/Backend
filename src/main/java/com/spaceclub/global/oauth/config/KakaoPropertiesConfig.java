package com.spaceclub.global.oauth.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {KakaoOauthProperties.class})
public class KakaoPropertiesConfig {

}
