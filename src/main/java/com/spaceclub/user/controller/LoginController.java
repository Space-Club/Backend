package com.spaceclub.user.controller;

import com.spaceclub.club.service.ClubProvider;
import com.spaceclub.global.Authenticated;
import com.spaceclub.global.jwt.vo.JwtUser;
import com.spaceclub.user.controller.dto.UserCodeRequest;
import com.spaceclub.user.controller.dto.UserLoginResponse;
import com.spaceclub.user.controller.dto.UserRequiredInfoRequest;
import com.spaceclub.user.service.AccountService;
import com.spaceclub.user.service.UserService;
import com.spaceclub.user.service.vo.UserLoginInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class LoginController {

    private final AccountService accountService;

    private final ClubProvider clubProvider;

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserLoginResponse> createAccount(@RequestBody UserRequiredInfoRequest request) {
        userService.updateRequiredProfile(request.userId(), request.toRequiredProfile());
        UserLoginInfo userLoginInfo = accountService.createAccount(request.userId());
        UserLoginResponse response = UserLoginResponse.from(userLoginInfo);

        return ResponseEntity.created(URI.create("/api/v1/users/" + response.userId()))
                .body(response);
    }

    @PostMapping("/oauths")
    public UserLoginResponse loginUser(@RequestBody UserCodeRequest userCodeRequest) {
        LocalDateTime now = LocalDateTime.now();
        UserLoginInfo userLoginInfo = accountService.loginUser(userCodeRequest.code(), now);

        return UserLoginResponse.from(userLoginInfo);
    }

    @Deprecated
    @PostMapping("/logout")
    @ResponseStatus(OK)
    public void logout(@Authenticated JwtUser jwtUser) {
        accountService.logout(jwtUser.id());
    }

    @DeleteMapping
    @ResponseStatus(NO_CONTENT)
    public void deleteUser(@Authenticated JwtUser jwtUser) {
        Long userId = jwtUser.id();

        int numberOfClubsUserBelong = clubProvider.getNumberOfClubsUserBelong(userId);
        accountService.deleteUser(userId, numberOfClubsUserBelong);
    }

}
