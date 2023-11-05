package com.spaceclub.user.controller;

import com.spaceclub.event.domain.Event;
import com.spaceclub.global.dto.PageResponse;
import com.spaceclub.global.jwt.service.JwtService;
import com.spaceclub.user.controller.dto.UserEventGetResponse;
import com.spaceclub.user.controller.dto.UserLoginResponse;
import com.spaceclub.user.domain.User;
import com.spaceclub.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.spaceclub.user.controller.dto.UserEventGetResponse.from;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @GetMapping("/{userId}/events")
    public PageResponse<UserEventGetResponse, Event> getAllEvents(@PathVariable Long userId, Pageable pageable) {
        Page<Event> eventPages = userService.findAllEventPages(userId, pageable);

        List<UserEventGetResponse> eventGetResponse = eventPages.getContent().stream()
                .map(event -> from(event, userService.findEventStatus(userId, event)))
                .toList();

        return new PageResponse<>(eventGetResponse, eventPages);
    }

    @PostMapping("/oauths")
    public UserLoginResponse getKaKaoCode(@RequestParam String code) {

        User kakaoUser = userService.createKakaoUser(code);

        if (kakaoUser.isNewMember()) {
            return UserLoginResponse.from("");
        }
        return UserLoginResponse.from(jwtService.createToken(kakaoUser.getId(), kakaoUser.getUsername()));
    }

}
