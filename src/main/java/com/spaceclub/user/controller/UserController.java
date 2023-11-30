package com.spaceclub.user.controller;

import com.spaceclub.global.Authenticated;
import com.spaceclub.global.bad_word_filter.BadWordFilter;
import com.spaceclub.global.jwt.vo.JwtUser;
import com.spaceclub.user.controller.dto.UserLoginResponse;
import com.spaceclub.user.controller.dto.UserProfileResponse;
import com.spaceclub.user.controller.dto.UserProfileUpdateRequest;
import com.spaceclub.user.service.AccountService;
import com.spaceclub.user.service.UserService;
import com.spaceclub.user.service.vo.RequiredProfile;
import com.spaceclub.user.service.vo.UserLoginInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/api/v1/me/profile")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final AccountService accountService;

    @GetMapping
    public UserProfileResponse getProfile(@Authenticated JwtUser jwtUser) {
        return userService.getProfile(jwtUser.id()).toResponse();
    }

    @PutMapping
    public ResponseEntity<UserLoginResponse> updateProfile(@RequestBody UserProfileUpdateRequest request, @Authenticated JwtUser jwtUser) {
        BadWordFilter.filter(request.name());
        RequiredProfile requiredProfile = new RequiredProfile(request.name(), request.phoneNumber());
        userService.updateRequiredProfile(jwtUser.id(), requiredProfile);
        UserLoginInfo accountInfo = accountService.createAccount(jwtUser.id());

        return ResponseEntity.ok(UserLoginResponse.from(accountInfo));
    }

    @DeleteMapping("/images")
    @ResponseStatus(NO_CONTENT)
    public void removeProfileImage(@Authenticated JwtUser jwtUser) {
        userService.removeUserProfileImage(jwtUser.id());
    }

    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(NO_CONTENT)
    public void changeProfileImage(
            @RequestPart MultipartFile userImage,
            @Authenticated JwtUser jwtUser
    ) {
        userService.changeUserProfileImage(userImage, jwtUser.id());
    }

}
