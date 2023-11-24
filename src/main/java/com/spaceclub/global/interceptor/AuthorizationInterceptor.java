package com.spaceclub.global.interceptor;

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

import static java.util.Collections.unmodifiableSet;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Component
@RequiredArgsConstructor
public class AuthorizationInterceptor implements HandlerInterceptor {

    private static final String TOKEN_PREFIX = "Bearer ";

    private static final Map<String, HttpMethod> PATH_TO_EXCLUDE = Map.of(
            "/api/v1/users", POST,
            "/api/v1/clubs/invites/", GET,
            "/api/v1/events", GET,
            "/api/v1/events/searches", GET,
            "/api/v1/users/oauth", POST
    );

    private final AccountService accountService;

    private final JwtManager jwtManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        for (Map.Entry<String, HttpMethod> entry : unmodifiableSet(PATH_TO_EXCLUDE.entrySet())) {
            boolean matchesPathAndMethod = request.getRequestURI().contains(entry.getKey()) &&
                    request.getMethod().equals(entry.getValue().name());

            if (matchesPathAndMethod) {
                return true;
            }
        }

        String header = request.getHeader(AUTHORIZATION).replace(TOKEN_PREFIX, "");
        Claims claims = jwtManager.getClaims(header);

        return accountService.isAuthenticatedUser(claims.getId(), claims.getUsername());
    }

}
