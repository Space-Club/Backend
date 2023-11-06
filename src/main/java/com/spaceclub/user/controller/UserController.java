package com.spaceclub.user.controller;

import com.spaceclub.event.domain.Event;
import com.spaceclub.global.dto.PageResponse;
import com.spaceclub.global.jwt.service.JwtService;
import com.spaceclub.user.controller.dto.UserCodeRequest;
import com.spaceclub.user.controller.dto.UserEventGetResponse;
import com.spaceclub.user.controller.dto.UserLoginResponse;
import com.spaceclub.user.controller.dto.UserProfileImageResponse;
import com.spaceclub.user.controller.dto.UserProfileResponse;
import com.spaceclub.user.controller.dto.UserRequiredInfoRequest;
import com.spaceclub.user.domain.User;
import com.spaceclub.user.service.UserService;
import com.spaceclub.user.service.vo.UserRequiredInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

import static com.spaceclub.user.controller.dto.UserEventGetResponse.from;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    private final UserService userService;

    private final JwtService jwtService;

    @GetMapping("/events")
    public PageResponse<UserEventGetResponse, Event> getAllEvents(Pageable pageable, HttpServletRequest servletRequest) {
        Long userId = jwtService.verifyUserId(servletRequest);
        Page<Event> eventPages = userService.findAllEventPages(userId, pageable);

        List<UserEventGetResponse> eventGetResponse = eventPages.getContent().stream()
                .map(event -> from(event, userService.findEventStatus(userId, event)))
                .toList();

        return new PageResponse<>(eventGetResponse, eventPages);
    }

    @PostMapping
    public ResponseEntity<UserLoginResponse> updateRequiredInfo(@RequestBody UserRequiredInfoRequest request) {//유저 찾기
        User user = userService.updateRequiredInfo(request.userId(), new UserRequiredInfo(request.name(), request.phoneNumber()));
        String accessToken = jwtService.createToken(user.getId(), user.getUsername());

        return ResponseEntity.created(URI.create("/api/v1/users/" + user.getId()))
                .body(UserLoginResponse.from(user.getId(), accessToken));
    }


    @PostMapping("/oauths")
    public UserLoginResponse loginUser(@RequestBody UserCodeRequest userCodeRequest) {
        User kakaoUser = userService.createKakaoUser(userCodeRequest.code());

        // 신규 유저면 빈 access token
        if (kakaoUser.isNewMember()) {
            return UserLoginResponse.from(kakaoUser.getId(), "");
        }

        // 기존 유저면 jwt
        String accessToken = jwtService.createToken(kakaoUser.getId(), kakaoUser.getUsername());

        return UserLoginResponse.from(kakaoUser.getId(), accessToken);
    }


    @GetMapping("/profiles")
    public UserProfileResponse getUserProfile(HttpServletRequest request) {
        Long userId = jwtService.verifyUserId(request);

        return userService.getUserProfile(userId).toResponse();
    }

    @GetMapping("/images")
    public UserProfileImageResponse getUserImage(HttpServletRequest request) {
        Long userId = jwtService.verifyUserId(request);

        return new UserProfileImageResponse(userService.getUserProfileImage(userId));
    }

}
