package com.spaceclub.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;

import java.util.List;

@ConfigurationProperties(prefix = "interceptor")
public record InterceptorProperties(
        String tokenPrefix,
        List<pathMethod> pathToExclude
) {

    public record pathMethod(
            String path,
            HttpMethod method
    ) {

    }

}
