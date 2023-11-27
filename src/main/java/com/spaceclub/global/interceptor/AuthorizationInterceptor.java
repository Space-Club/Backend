package com.spaceclub.global.interceptor;

import com.spaceclub.global.config.InterceptorProperties;
import com.spaceclub.global.config.InterceptorProperties.pathMethod;
import com.spaceclub.global.exception.TokenException;
import com.spaceclub.global.jwt.Claims;
import com.spaceclub.global.jwt.JwtManager;
import com.spaceclub.user.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.stream.Collectors;

import static com.spaceclub.global.exception.GlobalExceptionCode.BAD_REQUEST;
import static java.util.Collections.unmodifiableSet;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class AuthorizationInterceptor implements HandlerInterceptor {

    private final InterceptorProperties interceptorProperties;

    private final AccountService accountService;

    private final JwtManager jwtManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        System.out.println("interceptorProperties.tokenPrefix() = " + interceptorProperties.tokenPrefix());

        Map<String, HttpMethod> PATH_TO_EXCLUDE = interceptorProperties.pathToExclude().stream()
                .collect(Collectors.toMap(pathMethod::path, pathMethod::method));

        for (Map.Entry<String, HttpMethod> entry : unmodifiableSet(PATH_TO_EXCLUDE.entrySet())) {
            boolean matchesPathAndMethod = request.getRequestURI().contains(entry.getKey()) &&
                    request.getMethod().equals(entry.getValue().name());

            if (matchesPathAndMethod) {
                return true;
            }
        }

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        validateHeader(authorizationHeader);

        String header = authorizationHeader.replace(interceptorProperties.tokenPrefix(), "");
        Claims claims = jwtManager.getClaims(header);

        return accountService.isAuthenticatedUser(claims.getId(), claims.getUsername());
    }

    private void validateHeader(String authorizationHeader) {
        if (authorizationHeader == null) {
            throw new TokenException(BAD_REQUEST);
        }
    }

}
