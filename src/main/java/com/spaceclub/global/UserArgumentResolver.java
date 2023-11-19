package com.spaceclub.global;

import com.spaceclub.global.jwt.Claims;
import com.spaceclub.global.jwt.service.JwtManager;
import com.spaceclub.global.jwt.vo.JwtUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Objects;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    private final static String TOKEN_PREFIX ="Bearer ";

    private final JwtManager jwtManager;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return JwtUser.class.isAssignableFrom(parameter.getParameterType()) &&
                parameter.hasParameterAnnotation(Authenticated.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        String accessToken = Objects.requireNonNull(request)
                .getHeader(AUTHORIZATION)
                .replace(TOKEN_PREFIX, "");

        Claims claims = jwtManager.getClaims(accessToken);

        return new JwtUser(claims.getId(), claims.getUsername());
    }

}
